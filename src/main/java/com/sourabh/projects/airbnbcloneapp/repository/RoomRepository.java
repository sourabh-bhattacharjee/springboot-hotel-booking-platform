package com.sourabh.projects.airbnbcloneapp.repository;

import com.sourabh.projects.airbnbcloneapp.entity.Hotel;
import com.sourabh.projects.airbnbcloneapp.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoomRepository  extends JpaRepository<Room, Long> {
    Optional<Room> findByIdAndHotelId(Long roomId, Long hotelId);
}
