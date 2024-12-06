package com.markndevon.cardgames.controller;

import com.markndevon.cardgames.message.*;
import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.gamestates.GameState;
import com.markndevon.cardgames.model.player.Player;
import com.markndevon.cardgames.service.GameService;
import com.markndevon.cardgames.service.HeartsService;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
    Generic interface for game controller, handles communication from the client to manage the game state
 */
public abstract class GameController {
    protected final Map<Integer, GameService> gameRooms = new ConcurrentHashMap<>();

    public abstract StartGameRequest createGame(int gameId, RulesConfig rulesConfig, String username);
    public abstract PlayerJoinedMessage joinGame(int gameId, Player.PlayerDescriptor player);
    public abstract PlayCardMessage playCard(int gameId, PlayCardMessage cardMessage);
    public abstract List<GameService> getActiveGames();

    public int getCurrentPlayerIdForGame(int gameId) {
        // TODO: synchronization issues? Might want to use a atomic integer to track id
        // TODO: Return -1 if the game is full.
        return gameRooms.get(gameId).getPlayers().size();
    }

}
