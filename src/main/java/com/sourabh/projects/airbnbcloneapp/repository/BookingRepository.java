package com.sourabh.projects.airbnbcloneapp.repository;

import com.sourabh.projects.airbnbcloneapp.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    Boolean existsByRoomId(Long roomId);

    Optional<Booking> findByPaymentSessionId(String paymentSessionId);
}
