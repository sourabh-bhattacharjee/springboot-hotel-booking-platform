package com.sourabh.projects.airbnbcloneapp.dto;

import com.sourabh.projects.airbnbcloneapp.entity.HotelContactInfo;
import lombok.Data;

@Data
public class HotelDto {

    private long id;
    private String name;
    private String city;
    private String[] photos;
    private String[] amenities;
    private HotelContactInfo contactInfo;
    private Boolean active;
}
