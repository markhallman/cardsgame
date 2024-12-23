package com.markndevon.cardgames.message;

import com.markndevon.cardgames.model.Card;
import com.markndevon.cardgames.model.player.Player;

public class PlayCardMessage extends Message {

    private final String playerName;
    private final Card card;
    public PlayCardMessage(final String player, final Card card) {
        this.playerName = player;
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    public String getPlayerName() {
        return playerName;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.PlayCardMessage;
    }

}
