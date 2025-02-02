package com.markndevon.cardgames.service.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTService {

    //TODO: this is horrible, figure out how to store this securely
    private static final String SECRET_KEY = "37o22W51GVTUL0T953LeDj69ro52O1QZcmzZ9/6yFB8=";
    private final Key secret;
    private static final long ACCESS_EXPIRATION_TIME = 864_000_000; // 10 days

    @Autowired
    private UserDetailsService userDetailsService;

    public JWTService() {
        secret = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateAccessJWTToken(String username){
        return generateJWTToken(username, ACCESS_EXPIRATION_TIME);
    }

    public String generateJWTToken(String username, Long expirationTime) {
        Map<String, Object> claims = new HashMap<>();

        Date issuedTime = new Date(System.currentTimeMillis());
        Date expirationTimeDate = new Date(issuedTime.getTime() + expirationTime);

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(issuedTime)
                .expiration(expirationTimeDate)
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

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
        } catch(JwtException invalidTokenException){
            return false;
        }
        return !isTokenExpired(token);
    }

    public UserDetails getUserDetailsFromRequestAndValidate(HttpServletRequest request){

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("jwt".equals(cookie.getName())) {
                String token = cookie.getValue();
                if(validateToken(token)){
                    String username = extractUsername(token);
                    return userDetailsService.loadUserByUsername(username);
                }
            }
        }

        return null;
    }
}
