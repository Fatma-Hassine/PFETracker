package com.pfetracker.dto.module1;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReinitialisationRequest {
	 @NotBlank(message = "Le token est obligatoire")
	    private String token;

	    @NotBlank(message = "Le nouveau mot de passe est obligatoire")
	    @Size(min = 8, message = "Minimum 8 caractères")
	    private String nouveauMotDePasse;
}
