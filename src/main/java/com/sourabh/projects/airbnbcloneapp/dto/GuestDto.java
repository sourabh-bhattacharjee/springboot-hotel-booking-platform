package com.sourabh.projects.airbnbcloneapp.dto;

import com.sourabh.projects.airbnbcloneapp.entity.User;
import com.sourabh.projects.airbnbcloneapp.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class GuestDto {

    private long id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;
}
