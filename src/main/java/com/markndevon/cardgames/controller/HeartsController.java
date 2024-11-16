package com.markndevon.cardgames.controller;

import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.message.*;
import com.markndevon.cardgames.model.Card;
import com.markndevon.cardgames.model.config.HeartsRulesConfig;
import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.service.HeartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

/*
    Controller for a game of Hearts
 */
@Controller
public class HeartsController extends GameController {

    private Map<Integer, HeartsService> heartsGameRooms = new HashMap<>();
    @Autowired
    private Logger logger;

    public HeartsController() {
    }

    @Override
    @MessageMapping("/hearts/createGame")
    @SendTo("/topic/hearts/game-room")
    public StartGameRequest createGame(int gameId, RulesConfig heartsRulesConfig) {
        logger.log("Creating game with ID " + gameId);
        heartsGameRooms.put(gameId, new HeartsService(gameId, (HeartsRulesConfig) heartsRulesConfig));
        return new StartGameRequest(heartsRulesConfig);
    }

    @Override
    @MessageMapping("/hearts/joinGame")
    @SendTo("/topic/hearts/game-room")
    public PlayerJoinedMessage joinGame(PlayerJoinedMessage playerJoined, int gameId){
        return playerJoined;
    }

    @Override
    @MessageMapping("/hearts/{gameId}/playCard")
    @SendTo("/topic/hearts/game-room/{gameId}/playCard")
    public PlayCardMessage playCard(
            @DestinationVariable int gameId,
            @Payload PlayCardMessage cardMessage){
        if (!heartsGameRooms.containsKey(gameId)){
            throw new IllegalArgumentException("Invalid Game ID");
        }

        heartsGameRooms.get(gameId).playCard(cardMessage);
        return cardMessage;
    }

    /*
    @MessageMapping("/hearts/{gameId}/passCards")
    @SendTo("/topic/hearts/game-room/{gameId}/passCards")
    public PassCardsMessage passCards(
            @DestinationVariable int gameId,
            @Payload PassCardsMessage cardMessage){
        // TODO: Error handling?
        heartsGameRooms.get(gameId).passCards(cardMessage);
        return cardMessage;
    }
    */


    @MessageMapping("/hearts/{gameId}/chat")
    @SendTo("/topic/hearts/game-room/{gameId}/chat")
    public ChatMessage chatMessage(
            @DestinationVariable int gameId,
            @Payload ChatMessage chatMessage) {
        return chatMessage;
    }
}
