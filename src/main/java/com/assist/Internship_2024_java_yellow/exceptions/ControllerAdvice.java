package com.assist.Internship_2024_java_yellow.exceptions;

import com.assist.Internship_2024_java_yellow.dtos.ErrorObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(AuctionNotFoundException.class)
    public ResponseEntity<ErrorObject> handleAuctionNotFoundException(){

        ErrorObject errorObject = ErrorObject.builder().statusCode(HttpStatus.NOT_FOUND.value()).message("Auction not found.").build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorObject);
    }

    @ExceptionHandler(AuctionNotSavedException.class)
    public ResponseEntity<ErrorObject> handleAuctionNotSavedException(){

        ErrorObject errorObject = ErrorObject.builder().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).message("Auction could not be saved.").build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorObject);
    }

    @ExceptionHandler(AuctionNotUpdatedException.class)
    public ResponseEntity<ErrorObject> handleAuctionNotUpdatedException(){

        ErrorObject errorObject = ErrorObject.builder().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).message("Auction could not be updated.").build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorObject);
    }

    @ExceptionHandler(AuctionNotDeletedException.class)
    public ResponseEntity<ErrorObject> handleAuctionNotDeletedException(){

        ErrorObject errorObject = ErrorObject.builder().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).message("Auction could not be deleted.").build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorObject);
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ErrorObject> handleInvalidEmailException() {

        ErrorObject errorObject = ErrorObject.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message("The email address is not valid.").build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorObject);
    }

    @ExceptionHandler(InvalidFirstNameException.class)
    public ResponseEntity<ErrorObject> handleInvalidFirstNameException() {

        ErrorObject errorObject = ErrorObject.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message("The first name is not valid.").build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorObject);
    }

    @ExceptionHandler(InvalidLastNameException.class)
    public ResponseEntity<ErrorObject> handleInvalidLastNameException() {

        ErrorObject errorObject = ErrorObject.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message("The last name is not valid.").build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorObject);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorObject> handleInvalidPasswordException() {

        ErrorObject errorObject = ErrorObject.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message("The password is not valid.").build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorObject);
    }

    @ExceptionHandler(InvalidCompanyNameException.class)
    public ResponseEntity<ErrorObject> handleInvalidCompanyNameException() {

        ErrorObject errorObject = ErrorObject.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message("The company name is not valid.").build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorObject);
    }

    @ExceptionHandler(InvalidStartingDateException.class)
    public ResponseEntity<ErrorObject> handleInvalidStartingDateException() {

        ErrorObject errorObject = ErrorObject.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message("The starting date is not valid. It can't be earlier than the current date, later than the ending date or equal to the current date or ending date").build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorObject);
    }

    @ExceptionHandler(InvalidEndingDateException.class)
    public ResponseEntity<ErrorObject> handleInvalidEndingDateException() {

        ErrorObject errorObject = ErrorObject.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message("The ending date is not valid. The ending date can't be earlier than the current date.").build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorObject);
    }

    @ExceptionHandler(InvalidStartingPriceException.class)
    public ResponseEntity<ErrorObject> handleInvalidStartingPriceException() {

        ErrorObject errorObject = ErrorObject.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message("The starting price is not valid. The starting price can't be higher than the threshold price, a negative number or zero.").build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorObject);
    }

    @ExceptionHandler(InvalidThresholdPriceException.class)
    public ResponseEntity<ErrorObject> handleInvalidThresholdPriceException() {

        ErrorObject errorObject = ErrorObject.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message("The threshold price is not valid. The threshold price can't be a negative number or zero.").build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorObject);
    }

    @ExceptionHandler(InvalidBankAccountNumberException.class)
    public ResponseEntity<ErrorObject> handleInvalidBankAccountNumberException() {

        ErrorObject errorObject = ErrorObject.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message("The bank account number is not valid.").build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorObject);
    }

    @ExceptionHandler(InvalidTaxIdentificationNumberException.class)
    public ResponseEntity<ErrorObject> handleInvalidTaxIdentificationNumberException() {

        ErrorObject errorObject = ErrorObject.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message("The tax identification number is not valid.").build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorObject);
    }

    @ExceptionHandler(InvalidFileTypeAuctionFilesException.class)
    public ResponseEntity<ErrorObject> handleInvalidFileTypeAuctionFilesException() {

        ErrorObject errorObject = ErrorObject.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message("The file type is not supported. Only .svg, .jpg, .gif, .png, .pdf, .xls, .xlsx, .doc and .docx are accepted.").build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorObject);
    }

    @ExceptionHandler(InvalidFileTypeProfilePictureException.class)
    public ResponseEntity<ErrorObject> handleInvalidFileTypeProfilePictureException() {

        ErrorObject errorObject = ErrorObject.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message("The file type is not supported. Only .svg, .jpg, .gif and .png are accepted.").build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorObject);
    }

    @ExceptionHandler(FileNotSavedException.class)
    public ResponseEntity<ErrorObject> handleFileNotSavedException() {

        ErrorObject errorObject = ErrorObject.builder().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).message("The file could not be saved.").build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorObject);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorObject> handleFileNotFoundException() {

        ErrorObject errorObject = ErrorObject.builder().statusCode(HttpStatus.NOT_FOUND.value()).message("File not found.").build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorObject);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(500).body(ex.getMessage());
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailExistsException(EmailExistsException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return ResponseEntity.status(409).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Wrong credentials");
        return ResponseEntity.status(401).body(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Wrong credentials");
        return ResponseEntity.status(401).body(response);
    }

    @ExceptionHandler(InvalidCifException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCifException(InvalidCifException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    public ResponseEntity<Map<String, String>> handlePasswordNotMatchException(PasswordNotMatchException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler(InvalidResetTokenException.class)
    public ResponseEntity<Map<String, String>> handleInvalidResetTokenException(InvalidResetTokenException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return ResponseEntity.status(401).body(response);
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<ErrorObject> handleUserNotAuthenticatedException() {

        ErrorObject errorObject = ErrorObject.builder().statusCode(HttpStatus.FORBIDDEN.value()).message("The user is not authenticated.").build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorObject);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleRoleNotFoundException(RoleNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return ResponseEntity.status(404).body(response);
    }
}
