package com.pfetracker.exception.module1;
import org.springframework.http.HttpStatus;
public class ResourceNotFoundException extends BusinessException{
	public ResourceNotFoundException(String ressource, Long id) {
        super(ressource + " introuvable avec l'id : " + id,
              HttpStatus.NOT_FOUND,
              "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String ressource, String champ, String valeur) {
        super(ressource + " introuvable avec " + champ + " = " + valeur,
              HttpStatus.NOT_FOUND,
              "RESOURCE_NOT_FOUND");
    }
}
