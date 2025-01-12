package com.markndevon.cardgames.message;

import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.player.HumanPlayer;
import com.markndevon.cardgames.model.player.Player;

import java.util.List;

public class LobbyUpdateMessage extends Message{

    private final List<Player> players;
    private final RulesConfig rulesConfig;
    private final HumanPlayer gameOwner;

    public LobbyUpdateMessage(List<Player>  players, RulesConfig rulesConfig, HumanPlayer gameOwner){
        this.players = players;
        this.rulesConfig = rulesConfig;
        this.gameOwner = gameOwner;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public RulesConfig getRulesConfig() {
        return rulesConfig;
    }

    public HumanPlayer getGameOwner() { return gameOwner; }

    @Override
    public MessageType getMessageType() {
        return MessageType.LobbyUpdateMessage;
    }
}
