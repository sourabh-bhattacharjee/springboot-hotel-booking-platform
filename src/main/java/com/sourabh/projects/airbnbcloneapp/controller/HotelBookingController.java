package com.sourabh.projects.airbnbcloneapp.controller;

import com.sourabh.projects.airbnbcloneapp.dto.BookingDto;
import com.sourabh.projects.airbnbcloneapp.dto.BookingRequest;
import com.sourabh.projects.airbnbcloneapp.dto.GuestDto;
import com.sourabh.projects.airbnbcloneapp.service.BookingService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    public ResponseEntity<BookingDto> initializeBooking(@RequestBody BookingRequest bookingRequest) {
        return ResponseEntity.ok(bookingService.initializeBooking(bookingRequest));
    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuest(@PathVariable Long bookingId,
                                               @RequestBody List<GuestDto> guestDtoList) {
        return ResponseEntity.ok(bookingService.addGuest(bookingId,guestDtoList));
    }

    @PostMapping("/{bookingId}/payments")
    public ResponseEntity<Map<String, String>> initiatePayment(@PathVariable Long bookingId) {
        String sessionUrl = bookingService.initiatePayment(bookingId);
        return ResponseEntity.ok(Map.of("sessionUrl",sessionUrl));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) throws StripeException {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok().build();
    }

}
