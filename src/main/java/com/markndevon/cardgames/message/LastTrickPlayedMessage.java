package com.markndevon.cardgames.message;

import com.markndevon.cardgames.model.Card;
import com.markndevon.cardgames.model.player.Player;

import java.util.Map;

public class LastTrickPlayedMessage extends Message {
    private final Map<Player.PlayerDescriptor, Card> trick;

    public LastTrickPlayedMessage(final Map<Player.PlayerDescriptor, Card> trick) {
        this.trick = trick;
    }

    public Map<Player.PlayerDescriptor, Card> getTrick() {
        return trick;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.LastTrickPlayedMessage;
    }

}
