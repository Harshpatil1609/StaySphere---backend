package com.harshpatil.projects.airBnbProject.dto;

import com.harshpatil.projects.airBnbProject.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class GuestUpdateRequestDto {

    private Long id;
    private String name;
    private Gender gender;
    private Integer age;
}
