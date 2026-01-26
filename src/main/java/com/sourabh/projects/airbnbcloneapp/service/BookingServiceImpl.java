package com.sourabh.projects.airbnbcloneapp.service;

import com.sourabh.projects.airbnbcloneapp.dto.BookingDto;
import com.sourabh.projects.airbnbcloneapp.dto.BookingRequest;
import com.sourabh.projects.airbnbcloneapp.dto.GuestDto;
import com.sourabh.projects.airbnbcloneapp.entity.*;
import com.sourabh.projects.airbnbcloneapp.entity.enums.BookingStatus;
import com.sourabh.projects.airbnbcloneapp.exception.ResourceNotFoundException;
import com.sourabh.projects.airbnbcloneapp.exception.UnAuthorisedException;
import com.sourabh.projects.airbnbcloneapp.repository.*;
import com.sourabh.projects.airbnbcloneapp.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final GuestRepository guestRepository;
    private final PaymentCheckoutService paymentCheckoutService;
    private final PricingService pricingService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public BookingDto initializeBooking(BookingRequest bookingRequest) {
        log.info("Initializing booking for hotel : {}, room : {}, date {} - {} ", bookingRequest.getHotelId(),
                bookingRequest.getRoomId(), bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate() );

        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId()).orElseThrow(() ->
                new ResourceNotFoundException("Hotel with id " + bookingRequest.getHotelId() + " not found"));

        Room room = roomRepository.findById(bookingRequest.getRoomId()).orElseThrow(()->
                new ResourceNotFoundException("Room with id " + bookingRequest.getRoomId() + " not found"));

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(bookingRequest.getRoomId(),
                                                                                            bookingRequest.getCheckInDate(),
                                                                                            bookingRequest.getCheckOutDate(),
                                                                                            bookingRequest.getRoomsCount());
        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate()) + 1;

        if(inventoryList.size() != daysCount){
            throw new IllegalStateException("There are no inventory available for booking");
        }
        //update the booked count

        inventoryRepository.initBooking(bookingRequest.getRoomId(),
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(),
                bookingRequest.getRoomsCount());

        //calculate dynamic amount
        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(inventoryList);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));


       Booking booking = Booking.builder()
               .bookingStatus(BookingStatus.RESERVED)
               .hotel(hotel)
               .room(room)
               .checkInDate(bookingRequest.getCheckInDate())
               .checkOutDate(bookingRequest.getCheckOutDate())
               .user(getCurrentUser())
               .roomsCount(bookingRequest.getRoomsCount())
               .amount(totalPrice)
               .build();

       booking = bookingRepository.save(booking);

       return modelMapper.map(booking, BookingDto.class);

    }

    @Override
    @Transactional
    public BookingDto addGuest(Long bookingId, List<GuestDto> guestDtoList) {

        log.info("Adding guests for booking with id : {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()->
                new ResourceNotFoundException("Booking with id " + bookingId + " not found"));

        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthorisedException("Booking does not belong to this User with id: " + user.getId());
        }

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has already expired");
        }
        if(!booking.getBookingStatus().equals(BookingStatus.RESERVED)){
            throw new IllegalStateException("Booking status is not RESERVED, can not add guests");
        }

        for(GuestDto guestDto : guestDtoList){
            Guest guest = modelMapper.map(guestDto, Guest.class);
            guest.setUser(user);
            guest = guestRepository.save(guest);
            booking.getGuests().add(guest);
        }
        booking.setBookingStatus(BookingStatus.GUEST_ADDED);
        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);

    }

    @Override
    @Transactional
    public String initiatePayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking with id " + bookingId + " not found")
        );
        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthorisedException("Booking does not belong to this User with id: " + user.getId());
        }
        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has already expired");
        }
        String sessionUrl = paymentCheckoutService.getCheckoutSession(booking,frontendUrl+"/payments/success",frontendUrl+"/payments/faliure");
        booking.setBookingStatus(BookingStatus.PAYMENTS_PENDING);
        bookingRepository.save(booking);
        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        if("checkout.session.completed".equals(event.getType())){
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if(session == null) return;

            String sessionId = session.getId();
            Booking booking = bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(()-> new  ResourceNotFoundException("Booking not found for session Id"+ sessionId));
            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(),booking.getCheckInDate(), booking.getCheckOutDate(), booking.getRoomsCount());
            inventoryRepository.confirmBooking(booking.getRoom().getId(),booking.getCheckInDate(), booking.getCheckOutDate(), booking.getRoomsCount());
            log.info("Successfully captured payment for booking with id : {}", booking.getId());
        } else {
            log.warn("Unhandled event type : {}", event.getType());
        }
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) throws StripeException {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking with id " + bookingId + " not found")
        );
        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthorisedException("Booking does not belong to this User with id: " + user.getId());
        }
        if(!booking.getBookingStatus().equals(BookingStatus.CONFIRMED)){
            throw new IllegalStateException("Only confirmed Booking can be cancelled");
        }
        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(),booking.getCheckInDate(), booking.getCheckOutDate(), booking.getRoomsCount());
        inventoryRepository.cancelBooking(booking.getRoom().getId(),booking.getCheckInDate(), booking.getCheckOutDate(), booking.getRoomsCount());

        // handle the refund
        try{
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();
            Refund.create(refundParams);

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }



    }

    public boolean hasBookingExpired(Booking booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser(){
        return  (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
