package com.markndevon.cardgames.controller;

import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.model.authentication.CardGameUser;
import com.markndevon.cardgames.service.authentication.CardsUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

        return ResponseEntity.ok(returnUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody CardGameUser user) {
        logger.log("user " + user.getUsername() + " logging in");
        String retValue = userService.verify(user);

        if (retValue.equals(CardsUserService.LOGIN_FAILURE)) {
            logger.log("login failure");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(retValue);
        }
        logger.log("login success");

        return ResponseEntity.ok(retValue);
    }

    @GetMapping
    public ResponseEntity<String> refresh(){
        // TODO: Implement
        return ResponseEntity.ok("TODO IMPLEMENT");
    }
}
