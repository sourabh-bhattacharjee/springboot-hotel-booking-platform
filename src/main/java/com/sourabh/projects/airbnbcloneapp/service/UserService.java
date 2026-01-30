package com.sourabh.projects.airbnbcloneapp.service;

import com.sourabh.projects.airbnbcloneapp.dto.ProfileUpdateRequestDto;
import com.sourabh.projects.airbnbcloneapp.dto.UserDto;
import com.sourabh.projects.airbnbcloneapp.entity.User;

public interface UserService {

    User getUserById(Long userId);

    void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto);

    UserDto getMyProfile();
}
