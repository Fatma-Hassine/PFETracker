package com.pfetracker.exception.module1;

import org.springframework.http.HttpStatus;

public class InvitationAlreadySentException extends BusinessException{
	public InvitationAlreadySentException() {
        super("Une invitation est déjà en attente pour cet étudiant",
              HttpStatus.CONFLICT,
              "INVITATION_ALREADY_SENT");
    }
}
