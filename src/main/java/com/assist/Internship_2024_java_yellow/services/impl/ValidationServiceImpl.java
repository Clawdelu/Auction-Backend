package com.assist.Internship_2024_java_yellow.services.impl;

import com.assist.Internship_2024_java_yellow.dtos.IBANValidationResponse;
import com.assist.Internship_2024_java_yellow.entities.User;
import com.assist.Internship_2024_java_yellow.exceptions.InvalidBankAccountNumberException;
import com.assist.Internship_2024_java_yellow.exceptions.UserNotAuthenticatedException;
import com.assist.Internship_2024_java_yellow.repository.UserRepository;
import com.assist.Internship_2024_java_yellow.services.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {

    private final WebClient.Builder webClientBuilder;

    private final UserRepository userRepository;

    @Value("${iban.validator.api.key}")
    private String IBANValidatorApiKey;

    @Value("${iban.validator.endpoint}")
    private String IBANValidatorEndpoint;

    public boolean validateEmail(String email) {

        Pattern emailPattern = Pattern.compile("^(?i)(?=.{1,256})(?=.{1,64}@.{1,255}$)(?!.*[.]{2,})[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

        return emailPattern.matcher(email).matches();
    }

    public boolean validateFirstName(String firstName) {

        Pattern firstNamePattern = Pattern.compile("^([A-Z][a-zÀ-ÖØ-ÿ'-]*(?: [A-Z][a-zÀ-ÖØ-ÿ'-]*)*)$");

        return firstNamePattern.matcher(firstName).matches();
    }

    public boolean validateLastName(String lastName) {

        Pattern lastNamePattern = Pattern.compile("^([A-Z][a-zÀ-ÖØ-ÿ'-]*)(?: [A-Z][a-zÀ-ÖØ-ÿ'-]*)*(?:-[A-Z][a-zÀ-ÖØ-ÿ'-]*)*$");

        return lastNamePattern.matcher(lastName).matches();
    }

    public boolean validatePassword(String password) {

        Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{10,}$");

        return passwordPattern.matcher(password).matches();
    }

    public boolean validateCompanyName(String companyName) {

        Pattern companyNamePattern = Pattern.compile("^[A-Za-zÀ-ÖØ-ÿ0-9' -]+$");

        return companyNamePattern.matcher(companyName).matches();
    }

    public boolean validateBankAccountNumber(String bankAccountNumber) {

        String url = String.format("%s%s?api_key=%s", IBANValidatorEndpoint, bankAccountNumber, IBANValidatorApiKey);

        WebClient webClient = webClientBuilder.build();

        try {
            Mono<IBANValidationResponse> response = webClient
                    .post()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(IBANValidationResponse.class);

            IBANValidationResponse ibanValidationResponse = response.block();

            assert ibanValidationResponse != null;

            return ibanValidationResponse.getResult() == 200;
        }
        catch (RuntimeException e) {

            throw new InvalidBankAccountNumberException("Invalid bank account number");
        }
        
    }

    public boolean validateStartingDate(OffsetDateTime startingDate, OffsetDateTime endingDate) {

        return startingDate.isBefore(endingDate) && startingDate.isAfter(OffsetDateTime.now());
    }

    public boolean validateEndingDate(OffsetDateTime endingDate) {

        return endingDate.isAfter(OffsetDateTime.now());
    }

    public boolean validateStartingPrice(double startingPrice, double thresholdPrice) {

        return startingPrice < thresholdPrice && startingPrice > 0;
    }

    public boolean validateThresholdPrice(double thresholdPrice) {

        return thresholdPrice > 0;
    }

    public boolean validateFileTypeProfilePicture(String fileType) {

        List<String> validContentType = List
                .of("image/jpeg", "image/png", "image/gif", "image/svg+xml");

        return validContentType.contains(fileType);
    }

    public boolean validateFileTypeAuctionFiles(String fileType) {

        List<String> validContentType = List
                    .of("image/jpeg", //jpeg
                        "image/png",  //png
                        "image/gif",  //gif
                        "image/svg+xml",  //svg
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",  //docx
                        "application/msword", //doc
                        "application/vnd.ms-excel",  //xls
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",  //xlsx
                        "application/pdf");  //pdf

        return validContentType.contains(fileType);
    }

    @Override
    public User validateUserIsAuthenticated() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User contextUser = userRepository.findByEmail(authentication.getName());

        if (contextUser == null) {

            throw new UserNotAuthenticatedException("User is not authenticated.");
        }

        return contextUser;
    }
}
