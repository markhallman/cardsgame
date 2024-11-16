package com.markndevon.cardgames.message;

import com.markndevon.cardgames.model.Card;
import com.markndevon.cardgames.model.player.Player;

import java.util.Map;

public class UpdateCurrentTrickMessage extends Message {
    private final Map<Player.PlayerDescriptor, Card> currentTrick;

    public UpdateCurrentTrickMessage(final Map<Player.PlayerDescriptor, Card> currentTrick) {
        this.currentTrick = currentTrick;
    }

    public Map<Player.PlayerDescriptor, Card> getCurrentTrick() {
        return currentTrick;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.UpdateCurrentTrickMessage;
    }

}
