package com.markndevon.cardgames.message;

import com.markndevon.cardgames.model.Card;

import java.util.List;

public class PassCardsMessage extends Message {
    private final String passer;
    private final String receiver;
    private final List<Card> cards;

    public PassCardsMessage(final String passer, final String receiver, final List<Card> cards) {
        this.passer = passer;
        this.receiver = receiver;
        this.cards = cards;
    }

    public String getPasser() {
        return passer;
    }
    public String getReceiver() {
        return receiver;
    }
    public List<Card> getCards() {
        return cards;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.PassCardsMessage;
    }

}
