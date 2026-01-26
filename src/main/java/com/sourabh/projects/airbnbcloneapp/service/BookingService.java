package com.sourabh.projects.airbnbcloneapp.service;

import com.sourabh.projects.airbnbcloneapp.dto.BookingDto;
import com.sourabh.projects.airbnbcloneapp.dto.BookingRequest;
import com.sourabh.projects.airbnbcloneapp.dto.GuestDto;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;

import java.util.List;

public interface BookingService {


    BookingDto initializeBooking(BookingRequest bookingRequest);

    BookingDto addGuest(Long bookingId, List<GuestDto> guestDtoList);

    String initiatePayment(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId) throws StripeException;
}
