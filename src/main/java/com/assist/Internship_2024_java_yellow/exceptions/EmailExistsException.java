package com.assist.Internship_2024_java_yellow.exceptions;

public class EmailExistsException extends RuntimeException{
    public EmailExistsException(String message){
        super(message);
    }
}
