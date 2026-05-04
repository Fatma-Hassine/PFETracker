package com.pfetracker.exception.module1;

import org.springframework.http.HttpStatus;

public class DepartementMismatchException extends BusinessException{
	 public DepartementMismatchException() {
	        super("Accès refusé : vous ne pouvez gérer que votre propre département",
	              HttpStatus.FORBIDDEN,
	              "DEPARTEMENT_MISMATCH");
	    }
}
