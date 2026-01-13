package com.sourabh.projects.airbnbcloneapp.dto;

import com.sourabh.projects.airbnbcloneapp.entity.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelPriceDto {

    private Hotel hotel;
    private Double Price;
}
