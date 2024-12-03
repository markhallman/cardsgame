package com.markndevon.cardgames.message;

import com.markndevon.cardgames.model.player.Player;
import com.markndevon.cardgames.service.GameService;

import java.util.List;

public class ActivePlayersMessage extends Message{
    private final List<Player.PlayerDescriptor> players;
    public ActivePlayersMessage(final List<Player.PlayerDescriptor> players) {
        this.players = players;
    }

    public List<Player.PlayerDescriptor> getActivePlayers() {
        return players;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.ActivePlayersMessage;
    }
}
