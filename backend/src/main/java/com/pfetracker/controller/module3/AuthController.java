package com.pfetracker.controller.module3;

import com.pfetracker.dto.module3.AuthResponse;
import com.pfetracker.dto.module3.LoginRequest;
import com.pfetracker.entity.module3.User;
import com.pfetracker.repository.module3.UserRepository;
import com.pfetracker.security.module3.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v3/public/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @GetMapping("/test")
    public List<User> test() {
        return userRepository.findAll();
    }

    @GetMapping("/generate")
    public String generatePassword() {
        return passwordEncoder.encode("123456");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("Login attempt for email: " + loginRequest.getEmail());
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);

        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            System.out.println("User not found or password mismatch");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Identifiants incorrects ou compte inactif");
        }

        System.out.println("User found: " + user.getEmail() + " active: " + user.getIsActive());

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            System.out.println("User is not active");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Identifiants incorrects ou compte inactif");
        }

        String roleStr = user.getRole().name();

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), roleStr);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail(), roleStr);

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(roleStr)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody String refreshToken) {
        refreshToken = refreshToken.replace("\"", "");

        if (jwtTokenProvider.validateToken(refreshToken)) {
            Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);
            String role = jwtTokenProvider.getRoleFromToken(refreshToken);

            String newToken = jwtTokenProvider.generateToken(userId, email, role);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId, email, role);

            AuthResponse response = AuthResponse.builder()
                    .token(newToken)
                    .refreshToken(newRefreshToken)
                    .id(userId)
                    .email(email)
                    .role(role)
                    .build();

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
    }
}