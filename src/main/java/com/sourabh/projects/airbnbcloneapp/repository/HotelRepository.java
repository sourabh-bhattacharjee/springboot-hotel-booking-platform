package com.sourabh.projects.airbnbcloneapp.repository;

import com.sourabh.projects.airbnbcloneapp.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {


}
