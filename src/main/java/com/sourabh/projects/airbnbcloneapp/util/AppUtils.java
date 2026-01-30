package com.sourabh.projects.airbnbcloneapp.util;

import com.sourabh.projects.airbnbcloneapp.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class AppUtils {
    public static User getCurrentUser(){
        return  (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
