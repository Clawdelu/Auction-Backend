package com.assist.Internship_2024_java_yellow.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusEnum {

    Pending("Pending"),
    Ongoing("Ongoing"),
    Finished("Finished"),
    Rejected("Rejected"),
    Starting("Starting");

    private final String statusValue;
}
