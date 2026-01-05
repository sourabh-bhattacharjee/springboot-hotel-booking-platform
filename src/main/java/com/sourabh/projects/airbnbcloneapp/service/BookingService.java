package com.sourabh.projects.airbnbcloneapp.service;

import com.sourabh.projects.airbnbcloneapp.dto.BookingDto;
import com.sourabh.projects.airbnbcloneapp.dto.BookingRequest;
import com.sourabh.projects.airbnbcloneapp.dto.GuestDto;

import java.util.List;

public interface BookingService {


    BookingDto initializeBooking(BookingRequest bookingRequest);

    BookingDto addGuest(Long bookingId, List<GuestDto> guestDtoList);
}
