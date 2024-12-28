package com.markndevon.cardgames.repo;

import com.markndevon.cardgames.model.CardGameUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<CardGameUser, Integer> {
    CardGameUser findByUsername(String username);
}
