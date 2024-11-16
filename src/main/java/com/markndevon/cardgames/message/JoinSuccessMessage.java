package com.markndevon.cardgames.message;


/**
 * Message to be sent by the server to the joining client to confirm the client has been successfully added to the game
 */
public class JoinSuccessMessage extends Message {
    private final int id;
    private final boolean isLobbyHead;
    public JoinSuccessMessage(final int id, final boolean isLobbyHead) {
        this.id = id;
        this.isLobbyHead = isLobbyHead;
    }

    public int getId() {
        return id;
    }
    public boolean getIsFirstPlayer() { return isLobbyHead; }

    @Override
    public MessageType getMessageType() {
        return MessageType.JoinSuccessMessage;
    }

}
