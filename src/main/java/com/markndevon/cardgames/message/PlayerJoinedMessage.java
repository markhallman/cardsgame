package com.markndevon.cardgames.message;

import com.markndevon.cardgames.model.player.Player;

/**
 * Message to be sent to ALL clients when a new player has joined the lobby
 */
public class PlayerJoinedMessage extends Message {
    private final Player.PlayerDescriptor[] players;
    private final int gameId;
    public PlayerJoinedMessage(final Player.PlayerDescriptor[] players, int gameId) {

        this.players = players;
        this.gameId = gameId;
    }

    public Player.PlayerDescriptor[] getPlayers() {
        return players;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.PlayerJoinedMessage;
    }

}
