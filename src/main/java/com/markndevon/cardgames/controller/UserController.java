package com.markndevon.cardgames.controller;

import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.model.authentication.CardGameUser;
import com.markndevon.cardgames.service.authentication.CardsUserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public CardGameUser register(@RequestBody CardGameUser user){
        return userService.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody CardGameUser user){
        return userService.verify(user);
    }
}
