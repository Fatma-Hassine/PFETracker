package com.pfetracker.exception.module1;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends BusinessException{
	 public InvalidTokenException() {
	        super("Token invalide ou expiré",
	              HttpStatus.UNAUTHORIZED,
	              "INVALID_TOKEN");
	    }

	    public InvalidTokenException(String message) {
	        super(message, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
	    }
}
