package com.pfetracker.security.module3;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;


    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // ── Extraction ────────────────────────────────────────────────────────────

    public Long getUserIdFromToken(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    public String getRoleFromToken(String token) {
        return parseToken(token).get("role", String.class);
    }

    public String getEmailFromToken(String token) {
        return parseToken(token).getSubject();
    }


    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()             
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);  
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }


    public String generateToken(Long userId, String email, String role) {
        Date now        = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(email)                    // ✅ API 0.11.5
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(now)                     // ✅ API 0.11.5
                .setExpiration(expiryDate)            // ✅ API 0.11.5
                .signWith(getSigningKey(),
                        SignatureAlgorithm.HS512)     // ✅ API 0.11.5
                .compact();
    }


    private Claims parseToken(String token) {
        return Jwts.parserBuilder()                  // ✅ API 0.11.5
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)               // ✅ API 0.11.5
                .getBody();                          // ✅ API 0.11.5
    }
}