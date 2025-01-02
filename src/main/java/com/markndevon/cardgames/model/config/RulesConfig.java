package com.markndevon.cardgames.model.config;

public abstract class RulesConfig {
    protected int numPlayers; // Number of players in the game
    public RulesConfig(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public int getNumPlayers() {
        return numPlayers;
    }
}
