package com.markndevon.cardgames.controller;

import com.markndevon.cardgames.message.GameStartMessage;
import com.markndevon.cardgames.message.PlayCardMessage;
import com.markndevon.cardgames.message.PlayerJoinedMessage;
import com.markndevon.cardgames.message.StartGameRequest;
import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.player.Player;
import com.markndevon.cardgames.service.GameService;
import org.springframework.stereotype.Controller;

/*
    Generic interface for game controller, handles communication from the client to manage the game state
 */
public abstract class GameController {
    public abstract StartGameRequest createGame(int gameId, RulesConfig rulesConfig);
    public abstract PlayerJoinedMessage joinGame(int gameId, Player.PlayerDescriptor player);
    public abstract PlayCardMessage playCard(int gameId, PlayCardMessage cardMessage);

}
