package com.markndevon.cardgames.controller;

import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.model.authentication.CardGameUser;
import com.markndevon.cardgames.service.authentication.CardsUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private CardsUserService userService;

    @Autowired
    private final Logger logger = Logger.getInstance();

    @PostMapping("/register")
    public ResponseEntity<CardGameUser> register(@RequestBody CardGameUser user){
        CardGameUser returnUser;
        try {
            returnUser = userService.register(user);
        } catch(IllegalArgumentException ex){
            return ResponseEntity.status(409).body(new CardGameUser());
        }

        return ResponseEntity.ok(userService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody CardGameUser user) {
        String retValue = userService.verify(user);
        if (retValue.equals(CardsUserService.LOGIN_FAILURE)) {
            return ResponseEntity.status(401).body(retValue);
        }

        return ResponseEntity.ok(retValue);
    }
}
