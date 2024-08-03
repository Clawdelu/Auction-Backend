package com.assist.Internship_2024_java_yellow.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EntitySizeEnum {

    Small("Small"),
    Medium("Medium"),
    Big("Big");

    private final String entitySizeValue;
}
