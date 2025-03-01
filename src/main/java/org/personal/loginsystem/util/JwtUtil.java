package org.personal.loginsystem.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret.auth}")
    private String authSecretKey;

    @Value("${jwt.secret.reset}")
    private String passwordSecretKey;

    public Key getSigningKey(boolean isAuthToken) {
        String secretKey = isAuthToken ? authSecretKey : passwordSecretKey;
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(String usernameOrEmail, Long expirationTime, boolean isAuthToken) {
        return Jwts.builder()
                .setSubject(usernameOrEmail)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(isAuthToken), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, boolean isAuthToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(isAuthToken))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameOrEmailFromToken(String token, boolean isAuthToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(isAuthToken))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
