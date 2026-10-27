package com.harshpatil.projects.airBnbProject.util;

import com.harshpatil.projects.airBnbProject.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class AppUtils {
    public static User getCurrentUser()
    {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
