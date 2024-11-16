package com.markndevon.cardgames.message;

import com.markndevon.cardgames.model.Card;
import java.util.List;

public class KittyMessage extends Message {

    private final List<Card> kitty;

    public KittyMessage(final List<Card> kitty) {
        this.kitty = kitty;
    }

    public List<Card> getKitty() {
        return kitty;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.KittyMessage;
    }

}
