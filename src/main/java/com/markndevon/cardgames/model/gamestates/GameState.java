package com.markndevon.cardgames.model.gamestates;

import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.model.Card;
import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.player.Player;
import com.markndevon.cardgames.model.scoreboard.ScoreBoard;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public abstract class GameState {
    protected final Player[] players;
    protected final Player.PlayerDescriptor[] playerDescriptors;
    protected final RulesConfig rulesConfig;
    @Autowired
    protected final Logger logger;
    protected final ScoreBoard scoreBoard;

    public GameState(final Player[] players, final RulesConfig rulesConfig, Logger logger) {
        this.players = players;
        this.playerDescriptors = Arrays.stream(players)
                .map(Player::getPlayerDescriptor)
                .toArray(Player.PlayerDescriptor[]::new);
        this.rulesConfig = rulesConfig;
        this.logger = logger;
        if(players.length < 2 || players.length > 52) {
            throw new IllegalArgumentException("Can not create a game with less than 2 players or more than 52 players");
        }

        scoreBoard = new ScoreBoard(this){};
    }

    /**
     * Is this a legal play given the current state of the game?
     * @param cardToPlay the potential card play
     * @return boolean indicating whether this is a legal move or not
     */
    public abstract boolean isLegal(Player player, Card cardToPlay);

    public Player.PlayerDescriptor[] getPlayerDescriptors() {
        return playerDescriptors;
    }

    public RulesConfig getRulesConfig() {
        return rulesConfig;
    }
}
