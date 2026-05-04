package com.pfetracker.exception.module1;

import org.springframework.http.HttpStatus;

public class InvalidEmailDomainException extends BusinessException{
	 public InvalidEmailDomainException() {
	        super("Seuls les emails institutionnels sont autorisés",
	              HttpStatus.BAD_REQUEST,
	              "INVALID_EMAIL_DOMAIN");
	 }    
}
