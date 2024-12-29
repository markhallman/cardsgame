package com.markndevon.cardgames.service.authentication;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;

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
}
