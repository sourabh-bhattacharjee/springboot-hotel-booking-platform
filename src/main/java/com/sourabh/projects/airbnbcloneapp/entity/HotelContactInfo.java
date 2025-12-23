package com.sourabh.projects.airbnbcloneapp.entity;


import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class HotelContactInfo {

    private String address;
    private String phone;
    private String email;
    private String location;

}
