package com.pfetracker.exception.module1;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends BusinessException{
	public EmailAlreadyExistsException(String email) {
        super("L'adresse email est déjà utilisée : " + email,
              HttpStatus.CONFLICT,
              "EMAIL_ALREADY_EXISTS");
    }
}
