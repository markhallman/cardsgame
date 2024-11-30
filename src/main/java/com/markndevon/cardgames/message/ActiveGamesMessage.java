package com.markndevon.cardgames.message;

import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.service.GameService;

import java.util.List;

public class ActiveGamesMessage extends Message{

    private final List<GameService> gameServices;
    public ActiveGamesMessage(final List<GameService> gameServices) {
        this.gameServices = gameServices;
    }

    public List<GameService> getActiveGames() {
        return gameServices;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.ActiveGamesMessage;
    }
}
