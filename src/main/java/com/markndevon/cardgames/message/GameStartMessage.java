package com.markndevon.cardgames.message;

import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.player.Player;

public class GameStartMessage extends Message {

    private final RulesConfig rulesConfig;
    private final Player.PlayerDescriptor[] players;

    public GameStartMessage(final RulesConfig rulesConfig, final Player.PlayerDescriptor[] players) {
        this.rulesConfig = rulesConfig;
        this.players = players;
    }

    public RulesConfig getRulesConfig() {
        return rulesConfig;
    }

    public Player.PlayerDescriptor[] getPlayers() {
        return players;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.GameStartedMessage;
    }

}
