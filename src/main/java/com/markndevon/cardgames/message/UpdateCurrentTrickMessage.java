package com.markndevon.cardgames.message;

import com.hearts.message.json.JSONConstants;
import com.hearts.message.json.JSONConversions;
import com.hearts.play.Card;
import com.hearts.player.Player;
import org.json.simple.JSONObject;

import java.util.Map;

public class UpdateCurrentTrickMessage extends Message {
    private final Map<Player.PlayerDescriptor, Card> currentTrick;

    public UpdateCurrentTrickMessage(final Map<Player.PlayerDescriptor, Card> currentTrick) {
        this.currentTrick = currentTrick;
    }

    public Map<Player.PlayerDescriptor, Card> getCurrentTrick() {
        return currentTrick;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.UpdateCurrentTrickMessage;
    }

    @Override
    public String toJSONString(final JSONObject jsonObject) {
        final JSONObject currentTrickObj = new JSONObject();
        for(final Map.Entry<Player.PlayerDescriptor, Card> scoreUpdateEntry : currentTrick.entrySet()) {
            final JSONObject descriptorJSON = JSONConversions.encodePlayerDescriptor(scoreUpdateEntry.getKey());
            currentTrickObj.put(descriptorJSON, JSONConversions.encodeCard(scoreUpdateEntry.getValue()));
        }
        jsonObject.put(JSONConstants.MAP_KEY, currentTrickObj);

        return jsonObject.toString();
    }
}
