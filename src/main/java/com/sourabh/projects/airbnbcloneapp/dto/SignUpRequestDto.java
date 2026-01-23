package com.sourabh.projects.airbnbcloneapp.dto;

import lombok.Data;

@Data
public class SignUpRequestDto {

    private String email;
    private String password;
    private String name;
}
