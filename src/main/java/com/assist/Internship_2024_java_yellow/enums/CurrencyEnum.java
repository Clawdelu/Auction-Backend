package com.assist.Internship_2024_java_yellow.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CurrencyEnum {

    USD("USD"),
    RON("RON"),
    GBP("GBP"),
    CAD("CAD"),
    EUR("EUR"),
    AUD("AUD"),
    JPY("JPY"),
    CHF("CHF");

    private final String currencyCodeValue;
}
