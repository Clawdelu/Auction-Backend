package com.assist.Internship_2024_java_yellow.services.impl;


import com.assist.Internship_2024_java_yellow.auth.UserRegisterRequest;
import com.assist.Internship_2024_java_yellow.dtos.CompanyDTO;
import com.assist.Internship_2024_java_yellow.dtos.TaxIdentificationNumberDTO;
import com.assist.Internship_2024_java_yellow.entities.Company;
import com.assist.Internship_2024_java_yellow.entities.User;
import com.assist.Internship_2024_java_yellow.enums.EntitySizeEnum;
import com.assist.Internship_2024_java_yellow.enums.EntityTypeEnum;
import com.assist.Internship_2024_java_yellow.exceptions.InvalidBankAccountNumberException;
import com.assist.Internship_2024_java_yellow.exceptions.InvalidCifException;
import com.assist.Internship_2024_java_yellow.exceptions.InvalidCompanyNameException;
import com.assist.Internship_2024_java_yellow.mappers.CompanyMapper;
import com.assist.Internship_2024_java_yellow.repository.CompanyRepository;
import com.assist.Internship_2024_java_yellow.services.CompanyService;
import com.assist.Internship_2024_java_yellow.services.UserService;
import com.assist.Internship_2024_java_yellow.services.ValidationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Log
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final ValidationService validationService;
    private static final String apiUrl = "https://api.openapi.ro/api/validate/cif/";
    private final RestTemplate restTemplate;

    @Value("${api.key}")
    private String apiKey;

    @Override
    public Company createCompany(UserRegisterRequest userRegisterRequest, User user) {
            validateCompanyFields(userRegisterRequest);
            Company company = companyMapper.INSTANCE.toCompany(userRegisterRequest);
            company.setUser(user);
            return companyRepository.save(company);

    }

    public static boolean isValidCIF(String cif) {
        if (cif.toUpperCase().startsWith("RO")) {
            cif = cif.substring(2);
        }
        cif = cif.trim();
        if (!cif.matches("\\d{2,10}")) {
            return false;
        }

        int length = cif.length();
        int[] key = {7, 5, 3, 2, 1, 7, 5, 3, 2};
        int sum = 0;

        for (int i = length - 2, j = key.length - 1; i >= 0; i--, j--) {
            sum += Character.getNumericValue(cif.charAt(i)) * key[j];
        }

        int checkDigit = sum * 10 % 11;
        int controlDigit = checkDigit == 10 ? 0 : checkDigit;

        return controlDigit == Character.getNumericValue(cif.charAt(length - 1));
    }

    @Override
    public Boolean validateTaxIdentificationNumber(String taxIdentificationNumber) throws JsonProcessingException {
        String url = apiUrl + taxIdentificationNumber;
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        ObjectMapper mapper = new ObjectMapper();
        TaxIdentificationNumberDTO taxIdentificationNumberDTO = mapper.readValue(response.getBody(), TaxIdentificationNumberDTO.class);
        return taxIdentificationNumberDTO.getValid();

    }

    @Override
    public void validateCompanyFields(UserRegisterRequest userRegisterRequest) {

        if (!validationService.validateCompanyName(userRegisterRequest.getCompanyName())) {

            throw new InvalidCompanyNameException("Invalid company name.");
        }

        if (!isValidCIF(userRegisterRequest.getTaxIdentificationNumber())) {

            throw new InvalidCifException("Invalid CIF.");
        }

        try {
            if (!validateTaxIdentificationNumber(userRegisterRequest.getTaxIdentificationNumber()))
                throw new InvalidCifException("CIF doesn't exist.");
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        if (!validationService.validateBankAccountNumber(userRegisterRequest.getBankAccountNumber())) {

            throw new InvalidBankAccountNumberException("Invalid bank account number.");
        }
    }

    private void validateCompanyFields(CompanyDTO companyDTO) {

        if (!validationService.validateCompanyName(companyDTO.getCompanyName())) {

            throw new InvalidCompanyNameException("Invalid company name.");
        }

        if (!isValidCIF(companyDTO.getTaxIdentificationNumber())) {

            throw new InvalidCifException("Invalid CIF.");
        }

        if (!validationService.validateBankAccountNumber(companyDTO.getBankAccountNumber())) {

            throw new InvalidBankAccountNumberException("Invalid bank account number.");
        }
    }

    @Override
    public void editCompany(CompanyDTO companyDTO) {
        validateCompanyFields(companyDTO);
        User contextUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Company company = contextUser.getCompany();
        company.setCompanyName(companyDTO.getCompanyName());
        company.setEntityType(EntityTypeEnum.valueOf(companyDTO.getEntityType()));
        company.setEntitySize(EntitySizeEnum.valueOf(companyDTO.getEntitySize()));
        company.setCompanyAddress(companyDTO.getCompanyAddress());
        company.setBankAccountNumber(companyDTO.getBankAccountNumber());
        company.setTaxIdentificationNumber(companyDTO.getTaxIdentificationNumber());
        companyRepository.save(company);
    }
}
