package com.markndevon.cardgames.controller;

import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.message.*;
import com.markndevon.cardgames.model.Card;
import com.markndevon.cardgames.model.config.HeartsRulesConfig;
import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.player.Player;
import com.markndevon.cardgames.service.HeartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/*
    Controller for a game of Hearts
    // TODO: a chunk of this can probably be moved to the parent abstract class
 */
@Controller
public class HeartsController extends GameController {

    //TODO: Make sure game removals are handled correctly
    private Map<Integer, HeartsService> heartsGameRooms = new HashMap<>();
    @Autowired
    private Logger logger;

    public HeartsController() {
    }

    @Override
    @SendTo("/topic/hearts/game-room")
    public StartGameRequest createGame(int gameId, RulesConfig heartsRulesConfig) {
        logger.log("Creating game with ID " + gameId);
        heartsGameRooms.put(gameId, new HeartsService(gameId, (HeartsRulesConfig) heartsRulesConfig));
        //TODO: add the player who created the room

        return new StartGameRequest(heartsRulesConfig);
    }

    @MessageMapping("/hearts/game-room/{gameId}/createGame")
    @SendTo("/topic/hearts/game-room/{gameId}")
    public GameStartMessage startGame(@DestinationVariable int gameId) {
        logger.log("Starting game with ID " + gameId);
        HeartsService heartsService = heartsGameRooms.get(gameId);
        heartsService.startGame();
        return new GameStartMessage(heartsService.getRulesConfig(),
                heartsService.getPlayers().stream().map(Player::getPlayerDescriptor).toList().toArray(new Player.PlayerDescriptor[0]));
    }

    @Override
    @MessageMapping("/hearts/game-room/{gameId}/joinGame")
    @SendTo("/topic/hearts/game-room/{gameId}/joinGame")
    public PlayerJoinedMessage joinGame(@DestinationVariable int gameId,
                                        @Payload Player playerJoined){
        heartsGameRooms.get(gameId).addPlayer(playerJoined);
        return new PlayerJoinedMessage(playerJoined, gameId);
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
