package com.pfetracker.dto.module1;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String role;
    private Long userId;
    private String nomComplet;
    private boolean mustChangePassword;
}
