package com.markndevon.cardgames.controller;

import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.model.authentication.CardGameUser;
import com.markndevon.cardgames.service.authentication.CardsUserService;
import com.markndevon.cardgames.service.authentication.JWTService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${jwt.cookie.secure}")
    private boolean secureCookie;

    @Autowired
    private final Logger logger = Logger.getInstance();

    @Autowired
    private JWTService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody CardGameUser user){
        CardGameUser returnUser;
        try {
            returnUser = userService.register(user);
        } catch(IllegalArgumentException ex){
            return ResponseEntity.status(409).body(ex.getMessage());
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
        jwtCookie.setSecure(secureCookie);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60 * 60 * 24 * 10);
        jwtCookie.setAttribute("SameSite", "Lax"); // Important for cross-origin

        response.addCookie(jwtCookie);

        logger.log("Steting cookie: " + jwtCookie);

        return ResponseEntity.ok(CardsUserService.LOGIN_SUCCESS);
    }

    @GetMapping("/authenticated")
    public ResponseEntity<?> checkAuth(HttpServletRequest request){
        logger.log("Checking Auth for request");

        UserDetails userDetails = jwtService.getUserDetailsFromRequestAndValidate(request);

        if(userDetails == null){
            logger.log("Unauthorizred user");
            return ResponseEntity.status(401).body("Unauthorized user");
        } else {
            logger.log("User okay user");
            return ResponseEntity.ok(userDetails);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody CardGameUser user,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        Cookie nullJwtCookie = new Cookie("jwt", null);
        nullJwtCookie.setHttpOnly(true);
        nullJwtCookie.setSecure(secureCookie);
        nullJwtCookie.setPath("/");
        nullJwtCookie.setMaxAge(0); // Immediately expire the cookie
        response.addCookie(nullJwtCookie);

        logger.log("Nulling out cookie: " + nullJwtCookie);

        return ResponseEntity.ok().body(LOGOUT_SUCCESS);
    }
}
