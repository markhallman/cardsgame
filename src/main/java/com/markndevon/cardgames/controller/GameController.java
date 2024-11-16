package com.markndevon.cardgames.controller;

import com.markndevon.cardgames.message.PlayCardMessage;
import com.markndevon.cardgames.message.PlayerJoinedMessage;
import org.springframework.stereotype.Controller;

/*
    Generic interface for game controller, handles communication from the client to manage the game state
 */
@Controller
public abstract class GameController {
    public abstract void createGame(int gameId);
    public abstract PlayerJoinedMessage joinGame(PlayerJoinedMessage playerJoined, int gameId);
    public abstract void startGame();
    public abstract PlayCardMessage playCard(int roomId, PlayCardMessage cardMessage);

}
