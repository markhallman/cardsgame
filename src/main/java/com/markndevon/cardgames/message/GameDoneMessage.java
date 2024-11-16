package com.markndevon.cardgames.message;

import java.util.Map;

public class GameDoneMessage extends Message {

    private final Map<String, Integer> scoreBoard;

    public GameDoneMessage(final Map<String, Integer> scoreBoard) {
        this.scoreBoard = scoreBoard;
    }

    public Map<String, Integer> getScoreBoard() {
        return scoreBoard;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.GameDoneMessage;
    }

}
