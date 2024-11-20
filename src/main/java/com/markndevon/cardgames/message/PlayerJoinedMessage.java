package com.markndevon.cardgames.message;

import com.markndevon.cardgames.model.player.Player;

/**
 * Message to be sent to ALL clients when a new player has joined the lobby
 */
public class PlayerJoinedMessage extends Message {
    private final Player player;
    private final int gameId;
    public PlayerJoinedMessage(final Player player, int gameId) {

        this.player = player;
        this.gameId = gameId;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.PlayerJoinedMessage;
    }

}
