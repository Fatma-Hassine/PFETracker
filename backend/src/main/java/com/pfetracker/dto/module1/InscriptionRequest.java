package com.pfetracker.dto.module1;

import com.pfetracker.entity.module1.enums.Role;

import jakarta.validation.constraints.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscriptionRequest {
	@NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format email invalide")
    private String email;

    @NotBlank(message = "Le nom complet est obligatoire")
    private String nomComplet;

    @NotNull(message = "Le rôle est obligatoire")
    private Role role; 
}
