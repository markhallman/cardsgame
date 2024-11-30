package com.markndevon.cardgames.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.gamestates.GameState;
import com.markndevon.cardgames.model.player.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class GameService {
    protected final int gameId;
    protected RulesConfig rulesConfig;
    protected final List<Player> players = new ArrayList<>();

    protected boolean gameIsStarted = false;

    @JsonIgnore
    GameState gameState;

    public GameService(int gameId, RulesConfig rulesConfig){
        this.gameId = gameId;
        this.rulesConfig = rulesConfig;

    }
    public int getGameId() {
        return gameId;
    }

    public GameState getGameState() {
        // Will return null if the game has not started yet
        return gameState;
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
