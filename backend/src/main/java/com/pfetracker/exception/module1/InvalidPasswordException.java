package com.pfetracker.exception.module1;

import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends BusinessException{
	public InvalidPasswordException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_PASSWORD");
    }
}
