package com.markndevon.cardgames.message;

import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.player.Player;

import java.util.List;

public class LobbyUpdateMessage extends Message{

    private final List<Player> players;
    private final RulesConfig rulesConfig;

    public LobbyUpdateMessage(List<Player>  players, RulesConfig rulesConfig){
        this.players = players;
        this.rulesConfig = rulesConfig;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public RulesConfig getRulesConfig() {
        return rulesConfig;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.LobbyUpdateMessage;
    }
}
