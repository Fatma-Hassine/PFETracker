package com.pfetracker.dto.module1;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
	@NotBlank(message = "L'email est obligatoire")
    @Email
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;

    private String adresseIp;
}
