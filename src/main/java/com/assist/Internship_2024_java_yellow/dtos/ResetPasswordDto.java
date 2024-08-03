package com.assist.Internship_2024_java_yellow.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ResetPasswordDto {
    private String token;
    private String password;
    private String repeatPassword;
}
