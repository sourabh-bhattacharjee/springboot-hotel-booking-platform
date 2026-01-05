package com.sourabh.projects.airbnbcloneapp.dto;

import com.sourabh.projects.airbnbcloneapp.entity.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HotelInfoDto {
    private HotelDto hotel;
    private List<RoomDto> rooms;
}
