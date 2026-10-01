package com.pfetracker.repository.module3;

import com.pfetracker.entity.module3.GoogleOAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleOAuthTokenRepository extends JpaRepository<GoogleOAuthToken, Long> {
    Optional<GoogleOAuthToken> findByUserEmail(String userEmail);
    void deleteByUserEmail(String userEmail);
}
