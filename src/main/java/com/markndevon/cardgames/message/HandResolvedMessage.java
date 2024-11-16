package com.markndevon.cardgames.message;

public class HandResolvedMessage extends Message {

    public HandResolvedMessage() {
    }
    @Override
    public MessageType getMessageType() {
        return MessageType.HandResolvedMessage;
    }

}
