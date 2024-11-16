package com.markndevon.cardgames.message;

import com.markndevon.cardgames.model.Card;
import com.markndevon.cardgames.model.player.Player;

public class PlayCardMessage extends Message {

    private final Player.PlayerDescriptor playerDescriptor;
    private final Card card;
    public PlayCardMessage(final Player.PlayerDescriptor player, final Card card) {
        this.playerDescriptor = player;
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    public Player.PlayerDescriptor getPlayerDescriptor() {
        return playerDescriptor;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.PlayCardMessage;
    }

}
