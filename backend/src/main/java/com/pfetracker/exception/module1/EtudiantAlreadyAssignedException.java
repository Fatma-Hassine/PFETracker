package com.pfetracker.exception.module1;

import org.springframework.http.HttpStatus;

public class EtudiantAlreadyAssignedException extends BusinessException{
	public EtudiantAlreadyAssignedException() {
        super("Cet étudiant est déjà affecté à un encadrant",
              HttpStatus.CONFLICT,
              "ETUDIANT_ALREADY_ASSIGNED");
    }
}
