package com.markndevon.cardgames.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.gamestates.GameState;
import com.markndevon.cardgames.model.player.Player;
import com.markndevon.cardgames.model.player.RandomAIPlayer;

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
    public void setRulesConfig(RulesConfig rulesConfig) {
        this.rulesConfig = rulesConfig;
    }

    public synchronized List<Player> getPlayers(){
        return players;
    }

    public boolean getGameIsStarted(){return gameIsStarted; }

    public boolean gameIsFull() {
        // This will be true if there are more than the max number of players,
        // but it's not this classes job to manage that
        return players.size() == rulesConfig.getNumPlayers();
    }

    public synchronized void addPlayer(Player player) {
        if(gameIsStarted){
            throw new IllegalArgumentException("Game has already started, can't add more players");
        }

        if(gameIsFull()){
            throw new IllegalArgumentException("Game is full, can't add more players");
        }
        players.add(player);
    }

    public synchronized void removePlayer(Player player) {
        players.remove(player);

        if(gameIsStarted){
            possiblyFillPlayers();
        }
    }

    /**
     * Utility method for filling out a gameState with CPU players
     *
     * @return player array updated with CPU players
     */
    protected Player[] possiblyFillPlayers() {
        int size = getPlayers().size();

        for(int i = size; i < rulesConfig.getNumPlayers(); i++) {
            addPlayer(new RandomAIPlayer("AI" + i, i));
        }

        System.out.println("Filling up game: " + getPlayers());

        return getPlayers().toArray(new Player[0]);
    }

    public abstract void startGame();
    public abstract void updateClients();

    @Override
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        GameService service = (GameService) obj;

        // The game ID should be assigned uniquely to the service, so this should be a marker of identity
        return this.getGameId() == ((GameService) obj).getGameId();
    }
}
