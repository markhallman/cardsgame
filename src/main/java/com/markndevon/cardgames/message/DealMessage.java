package com.markndevon.cardgames.message;

import com.markndevon.cardgames.model.Card;

import java.util.List;

public class DealMessage extends Message {

    private final List<Card> cards;

    public DealMessage(final List<Card> cards) {
        this.cards = cards;
    }

    public List<Card> getCards() {
        return cards;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.DealMessage;
    }

}
