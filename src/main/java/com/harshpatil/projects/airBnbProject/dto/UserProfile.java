package com.harshpatil.projects.airBnbProject.dto;

import com.harshpatil.projects.airBnbProject.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {

    private String email;
    private String name;
    private Gender gender;
    private LocalDate dateOfBirth;
}
