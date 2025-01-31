package com.markndevon.cardgames.controller;

import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.model.authentication.CardGameUser;
import com.markndevon.cardgames.service.authentication.CardsUserDetailsService;
import com.markndevon.cardgames.service.authentication.CardsUserService;
import com.markndevon.cardgames.service.authentication.JWTService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.markndevon.cardgames.service.authentication.CardsUserService.LOGOUT_SUCCESS;

@RestController
public class UserController {

    @Autowired
    private CardsUserService userService;

    @Autowired
    private final Logger logger = Logger.getInstance();

    @Autowired
    private JWTService jwtService;

    @Autowired
    private CardsUserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<CardGameUser> register(@RequestBody CardGameUser user){
        CardGameUser returnUser;
        try {
            returnUser = userService.register(user);
        } catch(IllegalArgumentException ex){
            return ResponseEntity.status(409).body(new CardGameUser());
        }

        return ResponseEntity.ok(returnUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody CardGameUser user,
                                        HttpServletResponse response) {
        logger.log("user " + user.getUsername() + " logging in");
        String possibleToken = userService.verify(user);

        if (possibleToken.equals(CardsUserService.LOGIN_FAILURE)) {
            logger.log(CardsUserService.LOGIN_FAILURE);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(possibleToken);
        }
        logger.log(CardsUserService.LOGIN_SUCCESS);

        Cookie jwtCookie = new Cookie("jwt", possibleToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60 * 60 * 24 * 10);
        response.addCookie(jwtCookie);

        return ResponseEntity.ok(CardsUserService.LOGIN_SUCCESS);
    }

    @GetMapping("/authenticated")
    public ResponseEntity<?> checkAuth(HttpServletRequest request){

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        for (Cookie cookie : cookies) {
            if ("jwt".equals(cookie.getName())) {
                String token = cookie.getValue();
                String username = jwtService.extractUsername(token);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.validateToken(token, userDetails)) {
                    return ResponseEntity.ok(userDetails);
                }
            }
        }
        return ResponseEntity.status(401).body("Unauthorized user");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody CardGameUser user,
                                         HttpServletResponse response) {
        Cookie nullJwtCookie = new Cookie("jwt", null);
        nullJwtCookie.setHttpOnly(true);
        nullJwtCookie.setSecure(true);
        nullJwtCookie.setPath("/");
        nullJwtCookie.setMaxAge(0); // Immediately expire the cookie
        response.addCookie(nullJwtCookie);

        return ResponseEntity.ok().body(LOGOUT_SUCCESS);
    }

}
