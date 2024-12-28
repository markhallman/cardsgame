package com.markndevon.cardgames.service;

import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.model.CardGameUser;
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

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public CardGameUser register(CardGameUser user) {
        logger.log("Registering user: " + user.toString());
        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    public String verify(CardGameUser user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        if(authentication.isAuthenticated()){
            return "Login successful";
        }
        return "Login failed";
    }
}
