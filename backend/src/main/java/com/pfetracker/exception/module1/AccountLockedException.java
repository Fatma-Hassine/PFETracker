package com.pfetracker.exception.module1;

import org.springframework.http.HttpStatus;

public class AccountLockedException extends BusinessException{
	 public AccountLockedException() {
	        super("Compte verrouillé après trop de tentatives. Réessayez dans 30 minutes.",
	              HttpStatus.FORBIDDEN,
	              "ACCOUNT_LOCKED");
	    }

	    public AccountLockedException(String message) {
	        super(message, HttpStatus.FORBIDDEN, "ACCOUNT_LOCKED");
	    }
}
