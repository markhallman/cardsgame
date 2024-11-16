package com.markndevon.cardgames.service;

import com.markndevon.cardgames.model.config.RulesConfig;

public abstract class GameService {
    int gameId;
    RulesConfig rulesConfig;
    public GameService(int gameId, RulesConfig rulesConfig){
        this.gameId = gameId;
        this.rulesConfig = rulesConfig;
    }
}
