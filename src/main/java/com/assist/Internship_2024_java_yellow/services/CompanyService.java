package com.assist.Internship_2024_java_yellow.services;


import com.assist.Internship_2024_java_yellow.auth.UserRegisterRequest;
import com.assist.Internship_2024_java_yellow.dtos.CompanyDTO;
import com.assist.Internship_2024_java_yellow.entities.Company;
import com.assist.Internship_2024_java_yellow.entities.User;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface CompanyService {

    Company createCompany(UserRegisterRequest userRegisterRequest, User user);

    Boolean validateTaxIdentificationNumber(String taxIdentificationNumber) throws JsonProcessingException;

    void validateCompanyFields(UserRegisterRequest userRegisterRequest);

    void editCompany(CompanyDTO companyDTO);

    }