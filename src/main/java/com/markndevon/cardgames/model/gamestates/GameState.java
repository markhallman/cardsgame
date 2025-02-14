package com.markndevon.cardgames.model.gamestates;

import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.model.Card;
import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.player.Player;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public abstract class GameState {
    protected final int gameId;
    protected final Player[] players;
    protected final Player.PlayerDescriptor[] playerDescriptors;
    protected final RulesConfig rulesConfig;
    @Autowired
    protected final Logger logger;
    public GameState(final Player[] players, final RulesConfig rulesConfig, final int gameId, Logger logger) {
        this.players = players;
        this.playerDescriptors = Arrays.stream(players)
                .map(Player::getPlayerDescriptor)
                .toArray(Player.PlayerDescriptor[]::new);
        this.rulesConfig = rulesConfig;
        this.logger = logger;
        this.gameId = gameId;
        if(players.length < 2 || players.length > 52) {
            // TODO: may differ per game?
            throw new IllegalArgumentException("Can not create a game with less than 2 players or more than 52 players");
        }

    }

    /**
     * Is this a legal play given the current state of the game?
     * @param cardToPlay the potential card play
     * @return boolean indicating whether this is a legal move or not
     */
    public abstract boolean isLegal(Player player, Card cardToPlay);


    public abstract void start();

    public Player.PlayerDescriptor[] getPlayerDescriptors() {
        return playerDescriptors;
    }

    public RulesConfig getRulesConfig() {
        return rulesConfig;
    }
}
