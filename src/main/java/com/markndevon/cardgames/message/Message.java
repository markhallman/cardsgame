package com.markndevon.cardgames.message;

public abstract class Message {

    public abstract MessageType getMessageType();

    public String getMessageTypeValue() {
        return getMessageType().name();
    }

}
