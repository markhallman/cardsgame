package com.markndevon.cardgames.message;

import com.markndevon.cardgames.model.player.Player;

import java.util.Map;

public class UpdateScoreBoardMessage extends Message {
    private final Map<Player.PlayerDescriptor, Integer> scoreUpdate;

    public UpdateScoreBoardMessage(final Map<Player.PlayerDescriptor, Integer> scoreUpdate) {
        this.scoreUpdate = scoreUpdate;
    }

    public Map<Player.PlayerDescriptor, Integer> getScoreUpdate() {
        return scoreUpdate;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.UpdateScoreBoardMessage;
    }

}
