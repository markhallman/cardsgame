package com.markndevon.cardgames.message;


import com.hearts.message.json.JSONConstants;
import com.hearts.message.json.JSONConversions;
import com.hearts.play.Card;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

    @Override
    public String toJSONString(final JSONObject jsonObject) {
        final JSONArray cardArray = new JSONArray();
        for(final Card card : kitty) {
            cardArray.add(JSONConversions.encodeCard(card));
        }
        jsonObject.put(JSONConstants.CARD_ARRAY_KEY, cardArray);

        return jsonObject.toString();
    }
}
