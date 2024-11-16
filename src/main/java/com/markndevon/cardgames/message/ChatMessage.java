package com.markndevon.cardgames.message;

public class ChatMessage extends Message {

    private final String sender;
    private final String message;
    public ChatMessage(final String sender, final String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }
    public String getMessage() {
        return message;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.ChatMessage;
    }


}
