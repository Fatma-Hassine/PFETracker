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

    @GetMapping("/test")
    public List<User> test() {
        return userRepository.findAll();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("Login attempt for email: " + loginRequest.getEmail());
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);

        if (user == null) {
            System.out.println("User not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Identifiants incorrects ou compte inactif");
        }

        System.out.println("User found: " + user.getEmail() + " active: " + user.getIsActive());
        
        // Use Boolean.TRUE.equals to safely check if it's active, avoiding NPE if it's null
        if (!Boolean.TRUE.equals(user.getIsActive())) {
             System.out.println("User is not active");
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Identifiants incorrects ou compte inactif");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .build();

        return ResponseEntity.ok(response);
    }
}
