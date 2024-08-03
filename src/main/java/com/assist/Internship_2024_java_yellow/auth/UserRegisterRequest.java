package com.assist.Internship_2024_java_yellow.auth;

import com.assist.Internship_2024_java_yellow.enums.EntitySizeEnum;
import com.assist.Internship_2024_java_yellow.enums.EntityTypeEnum;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String companyName;
    private EntityTypeEnum entityType;
    private EntitySizeEnum entitySize;
    private String companyAddress;
    private String bankAccountNumber;
    private String taxIdentificationNumber;
}
