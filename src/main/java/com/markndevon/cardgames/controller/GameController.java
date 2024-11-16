package com.markndevon.cardgames.controller;

import com.markndevon.cardgames.message.GameStartMessage;
import com.markndevon.cardgames.message.PlayCardMessage;
import com.markndevon.cardgames.message.PlayerJoinedMessage;
import com.markndevon.cardgames.message.StartGameRequest;
import com.markndevon.cardgames.model.config.RulesConfig;
import org.springframework.stereotype.Controller;

/*
    Generic interface for game controller, handles communication from the client to manage the game state
 */
@Controller
public abstract class GameController {
    public abstract StartGameRequest createGame(int gameId, RulesConfig rulesConfig);
    public abstract PlayerJoinedMessage joinGame(PlayerJoinedMessage playerJoined, int gameId);
    public abstract PlayCardMessage playCard(int roomId, PlayCardMessage cardMessage);

}
