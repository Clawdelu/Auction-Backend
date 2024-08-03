package com.assist.Internship_2024_java_yellow.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleEnum {

    LEGAL("Legal"),
    ADMIN("Admin");

    private final String roleValue;
}
