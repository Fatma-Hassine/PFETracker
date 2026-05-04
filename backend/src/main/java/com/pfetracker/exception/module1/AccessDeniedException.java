package com.pfetracker.exception.module1;

import org.springframework.http.HttpStatus;
public class AccessDeniedException extends BusinessException{
	public AccessDeniedException() {
        super("Accès refusé : vous n'avez pas les droits nécessaires",
              HttpStatus.FORBIDDEN,
              "ACCESS_DENIED");
    }

    public AccessDeniedException(String message) {
        super(message, HttpStatus.FORBIDDEN, "ACCESS_DENIED");
    }
}
