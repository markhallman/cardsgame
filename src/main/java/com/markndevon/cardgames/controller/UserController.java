package com.markndevon.cardgames.controller;

import com.markndevon.cardgames.model.CardGameUser;
import com.markndevon.cardgames.service.CardsUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private CardsUserService userService;

    @PostMapping("/register")
    public CardGameUser register(@RequestBody CardGameUser user){
        return userService.register(user);
    }
}
