package com.markndevon.cardgames.message;

public class CreatePlayerMessage extends Message {

    private final String name;
    public CreatePlayerMessage(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.CreatePlayerMessage;
    }

}
