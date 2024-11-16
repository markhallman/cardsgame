package com.markndevon.cardgames.message;

import com.hearts.message.json.JSONConstants;
import org.json.simple.JSONObject;

public class ShowPassCardsDialogMessage extends Message {

    private final String receiver;
    private final int numCards;

    /**
     * Constructor for ShowPassCardsDialogMessage.
     * @param receiver name of the player to pass to
     * @param numCards number of cards to pass
     */
    public ShowPassCardsDialogMessage(final String receiver, final int numCards) {
        this.receiver = receiver;
        this.numCards = numCards;
    }

    public String getReceiver() {
        return receiver;
    }

    public int getNumCards() {
        return numCards;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.ShowPassCardsDialogMessage;
    }

    @Override
    public String toJSONString(final JSONObject jsonObject) {
        jsonObject.put(JSONConstants.PLAYER, receiver);
        jsonObject.put(JSONConstants.CARD_ARRAY_KEY, numCards);
        return jsonObject.toString();
    }
}
