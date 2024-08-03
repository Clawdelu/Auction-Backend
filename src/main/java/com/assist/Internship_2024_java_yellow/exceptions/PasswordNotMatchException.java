package com.assist.Internship_2024_java_yellow.exceptions;

public class PasswordNotMatchException extends RuntimeException{
    public PasswordNotMatchException(String message){
        super(message);
    }
}
