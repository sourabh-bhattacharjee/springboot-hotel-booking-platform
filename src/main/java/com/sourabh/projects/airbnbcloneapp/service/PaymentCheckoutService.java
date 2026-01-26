package com.sourabh.projects.airbnbcloneapp.service;

import com.sourabh.projects.airbnbcloneapp.entity.Booking;

public interface PaymentCheckoutService {
    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);
}
