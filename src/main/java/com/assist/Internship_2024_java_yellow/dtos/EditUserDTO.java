package com.assist.Internship_2024_java_yellow.dtos;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class EditUserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String repeatPassword;
}
