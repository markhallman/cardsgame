package com.markndevon.cardgames.service.authentication;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

@Service
public class JWTService {

    //TODO: this is horrible, figure out how to store this securely
    private static final String SECRET_KEY = "37o22W51GVTUL0T953LeDj69ro52O1QZcmzZ9/6yFB8=";
    private final Key secret;
    private static final long EXPIRATION_TIME = 864_000_000; // 10 days

    public JWTService() {
        secret = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
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
