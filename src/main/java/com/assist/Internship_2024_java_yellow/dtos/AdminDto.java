package com.assist.Internship_2024_java_yellow.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
