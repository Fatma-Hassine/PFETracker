package com.pfetracker.exception.module1;

import org.springframework.http.HttpStatus;

public class AccountDisabledException  extends BusinessException{

    public AccountDisabledException() {
        super("Compte non activé. En attente de validation par le responsable de département.",
              HttpStatus.FORBIDDEN,
              "ACCOUNT_DISABLED");
    }
}
