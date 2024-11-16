package com.markndevon.cardgames.message;


import com.hearts.message.json.JSONConstants;
import com.hearts.message.json.JSONConversions;
import com.hearts.play.Card;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

    @Override
    public String toJSONString(final JSONObject jsonObject) {
        final JSONArray cardArray = new JSONArray();
        for(final Card card : legalPlays) {
            cardArray.add(JSONConversions.encodeCard(card));
        }
        jsonObject.put(JSONConstants.CARD_ARRAY_KEY, cardArray);

        return jsonObject.toString();
    }
}
