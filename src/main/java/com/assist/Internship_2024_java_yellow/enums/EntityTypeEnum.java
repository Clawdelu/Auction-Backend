package com.assist.Internship_2024_java_yellow.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EntityTypeEnum {

    LLC("LLC"),
    Corporation("Corporation"),
    Partnership("Partnership"),
    SoleProprietorship("Sole Proprietorship");

    private final String entityTypeValue;
}
