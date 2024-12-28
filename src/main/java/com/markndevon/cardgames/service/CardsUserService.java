package com.markndevon.cardgames.service;

import com.markndevon.cardgames.model.CardGameUser;
import com.markndevon.cardgames.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CardsUserService {

    @Autowired
    private UserRepo repo;

    final private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public CardGameUser register(CardGameUser user){
        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }
}
