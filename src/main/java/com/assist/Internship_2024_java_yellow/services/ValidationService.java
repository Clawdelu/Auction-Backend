package com.assist.Internship_2024_java_yellow.services;

import com.assist.Internship_2024_java_yellow.entities.User;
import org.springframework.security.core.Authentication;

import java.time.OffsetDateTime;

public interface ValidationService {

    boolean validateEmail(String email);

    boolean validateFirstName(String firstName);

    boolean validateLastName(String lastName);

    boolean validatePassword(String password);

    boolean validateCompanyName(String companyName);

    boolean validateBankAccountNumber(String bankAccountNumber);

    boolean validateStartingDate(OffsetDateTime startingDate, OffsetDateTime endingDate);

    boolean validateEndingDate(OffsetDateTime startingDate);

    boolean validateStartingPrice(double startingPrice, double thresholdPrice);

    boolean validateThresholdPrice(double thresholdPrice);

    boolean validateFileTypeProfilePicture(String fileType);

    boolean validateFileTypeAuctionFiles(String fileType);

    User validateUserIsAuthenticated();
}
