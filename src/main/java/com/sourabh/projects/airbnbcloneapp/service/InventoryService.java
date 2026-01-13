package com.sourabh.projects.airbnbcloneapp.service;
import com.sourabh.projects.airbnbcloneapp.dto.HotelDto;
import com.sourabh.projects.airbnbcloneapp.dto.HotelPriceDto;
import com.sourabh.projects.airbnbcloneapp.dto.HotelSearchRequestDto;
import com.sourabh.projects.airbnbcloneapp.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);


    Page<HotelPriceDto> searchHotels(HotelSearchRequestDto hotelSearchRequestDto);
}
