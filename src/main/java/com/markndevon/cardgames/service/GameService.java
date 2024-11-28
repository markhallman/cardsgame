package com.markndevon.cardgames.service;

import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.player.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;

public abstract class GameService {
    final int gameId;
    RulesConfig rulesConfig;
    protected final List<Player> players = new ArrayList<>();

    public GameService(int gameId, RulesConfig rulesConfig){
        this.gameId = gameId;
        this.rulesConfig = rulesConfig;

    }
    public int getGameId() {
        return gameId;
    }

    public RulesConfig getRulesConfig() {
        return rulesConfig;
    }

    public List<Player> getPlayers(){
        return players;
    }

    public void addPlayer(Player player){
        players.add(player);
    }

    public abstract void startGame();
    public abstract void updateClients();
}
