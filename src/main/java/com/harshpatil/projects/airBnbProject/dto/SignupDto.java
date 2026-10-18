package com.harshpatil.projects.airBnbProject.dto;

import com.harshpatil.projects.airBnbProject.entity.enums.Role;
import com.harshpatil.projects.airBnbProject.entity.enums.Permissions;
import lombok.Data;

import java.util.Set;

@Data
public class SignupDto {

    private String email;
    private String pass;
    private String name;

}
