package com.markndevon.cardgames.service.authentication;

import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.model.authentication.CardGameUser;
import com.markndevon.cardgames.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CardsUserService {

    @Autowired
    private UserRepo repo;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private Logger logger;

    @Autowired
    private JWTService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public static final String LOGIN_FAILURE = "Login failed";
    public static final String LOGIN_SUCCESS = "Login success";
    public static final String LOGOUT_SUCCESS = "Logout success";

    public CardGameUser register(CardGameUser user) {
        if(repo.findByUsername(user.getUsername()) != null){
            throw new IllegalArgumentException("User with that username is already registered");
        }

        if(repo.findByEmail(user.getEmail()) != null){
            throw new IllegalArgumentException("User with that email is already registered");
        }

        logger.log("Registering user: " + user);
        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    public String verify(CardGameUser user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        if(authentication.isAuthenticated()){
            return jwtService.generateAccessJWTToken(user.getUsername());
        }
        return LOGIN_FAILURE;
    }
}
