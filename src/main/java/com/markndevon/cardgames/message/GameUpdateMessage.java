package com.markndevon.cardgames.message;

import com.markndevon.cardgames.model.gamestates.GameState;

public class GameUpdateMessage extends Message {
    final GameState currentGameState;

    public GameUpdateMessage(final GameState gameState) {
        this.currentGameState = gameState;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.GameStartedMessage;
    }

}
