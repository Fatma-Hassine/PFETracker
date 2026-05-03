package com.pfetracker.exception.module1;

import org.springframework.http.HttpStatus;

public class EncadrantCapacityException extends BusinessException{
	public EncadrantCapacityException(int limite) {
        super("L'encadrant a atteint sa capacité maximale de " + limite + " étudiant(s)",
              HttpStatus.CONFLICT,
              "ENCADRANT_CAPACITY_REACHED");
    }
}
