package com.markndevon.cardgames.message;

import com.markndevon.cardgames.model.Card;

import java.util.List;

public class UpdateLegalPlaysMessage extends Message {

    private final List<Card> legalPlays;

    public UpdateLegalPlaysMessage(final List<Card> legalPlays) {
        this.legalPlays = legalPlays;
    }

    public List<Card> getLegalPlays() {
        return legalPlays;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.UpdateLegalPlaysMessage;
    }

}
