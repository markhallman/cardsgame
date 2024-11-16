package com.markndevon.cardgames.message;

import com.hearts.message.json.JSONConversions;
import com.hearts.play.Card;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

import static com.hearts.message.json.JSONConstants.CARD_ARRAY_KEY;

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

    @Override
    public String toJSONString(final JSONObject jsonObject) {
        final JSONArray cardArray = new JSONArray();
        cards.forEach(card -> cardArray.add(JSONConversions.encodeCard(card)));
        jsonObject.put(CARD_ARRAY_KEY, cardArray);
        return jsonObject.toJSONString();
    }
}
