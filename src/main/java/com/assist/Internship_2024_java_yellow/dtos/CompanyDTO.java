package com.assist.Internship_2024_java_yellow.dtos;

import lombok.Data;

@Data
public class CompanyDTO {
    private String companyName;
    private String entityType;
    private String entitySize;
    private String companyAddress;
    private String bankAccountNumber;
    private String taxIdentificationNumber;
}
