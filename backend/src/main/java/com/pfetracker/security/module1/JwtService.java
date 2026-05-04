package com.pfetracker.security.module1;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.pfetracker.entity.module1.Utilisateur;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service("jwtServiceM1")
@Slf4j
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.access-token-expiration}")
    private long accessTokenExpiration;


    public String genererAccessToken(Utilisateur utilisateur) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role",          utilisateur.getRole().name());
        claims.put("nomComplet",    utilisateur.getNomComplet());
        claims.put("mustChangePwd", utilisateur.isMustChangePassword());
        return buildToken(claims, utilisateur.getEmail(), accessTokenExpiration);
    }

    private String buildToken(Map<String, Object> claims,
                               String subject,
                               long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) 
                .compact();
    }


    public String extraireEmail(String token) {
        return extraireClaim(token, Claims::getSubject);
    }

    public String extraireRole(String token) {
        return extraireClaim(token, c -> c.get("role", String.class));
    }

    public <T> T extraireClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extraireTousClaims(token));
    }

    private Claims extraireTousClaims(String token) {
        return Jwts.parserBuilder()          
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)      
                .getBody();                  
    }


    public boolean estValide(String token, Utilisateur utilisateur) {
        try {
            final String email = extraireEmail(token);
            return email.equals(utilisateur.getEmail()) && !estExpire(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token JWT invalide : {}", e.getMessage());
            return false;
        }
    }

    public boolean estExpire(String token) {
        return extraireExpiration(token).before(new Date());
    }

    private Date extraireExpiration(String token) {
        return extraireClaim(token, Claims::getExpiration);
    }


    private Key getSigningKey() {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}