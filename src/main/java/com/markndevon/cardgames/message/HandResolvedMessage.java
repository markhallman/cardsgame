package com.markndevon.cardgames.message;

import org.json.simple.JSONObject;

import static com.hearts.message.json.JSONConstants.HAND_DONE_KEY;

public class HandResolvedMessage extends Message {

    public HandResolvedMessage() {
    }
    @Override
    public MessageType getMessageType() {
        return MessageType.HandResolvedMessage;
    }

    @Override
    public String toJSONString(final JSONObject jsonObject) {
        jsonObject.put(HAND_DONE_KEY, "done");
        return jsonObject.toJSONString();
    }
}
