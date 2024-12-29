package com.markndevon.cardgames.service.authentication;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

@Service
public class JWTService {

    //TODO: how to store this securely
    private final Key secret;
    private static final long EXPIRATION_TIME = 864_000_000; // 10 days

    public JWTService() {
        try {
            KeyGenerator gen = KeyGenerator.getInstance("HmacSHA256");
            secret = gen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public String generateJWTToken(String username, String password) {
        Map<String, Object> claims = new HashMap<>();

        Date issuedTime = new Date(System.currentTimeMillis());
        Date expirationTime = new Date(issuedTime.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(issuedTime)
                .expiration(expirationTime)
                .and()
                .signWith(secret)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
