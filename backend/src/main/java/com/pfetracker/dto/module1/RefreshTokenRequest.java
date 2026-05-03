package com.pfetracker.dto.module1;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {
	@NotBlank(message = "Le refresh token est obligatoire")
    private String refreshToken;
}
