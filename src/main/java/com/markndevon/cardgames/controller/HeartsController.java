package com.markndevon.cardgames.controller;

import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.message.*;
import com.markndevon.cardgames.model.config.HeartsRulesConfig;
import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.gamestates.GameState;
import com.markndevon.cardgames.model.player.HumanPlayer;
import com.markndevon.cardgames.model.player.Player;
import com.markndevon.cardgames.service.GameService;
import com.markndevon.cardgames.service.GameServiceFactory;
import com.markndevon.cardgames.service.HeartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.markndevon.cardgames.model.gamestates.GameType.HEARTS;

/*
    Controller for a game of Hearts
    // TODO: a chunk of this can probably be moved to the parent abstract class
 */
@Controller
public class HeartsController extends GameController {

    //TODO: Make sure game removals are handled correctly. Scheduled task to remove inactive games?
    @Autowired
    private Logger logger;

    private final GameServiceFactory gameServiceFactory;

    public HeartsController(GameServiceFactory gameServiceFactory) {
        this.gameServiceFactory = gameServiceFactory;
    }

    @Override
    @SendTo("/topic/hearts/game-room")
    public StartGameRequest createGame(int gameId,
                                       RulesConfig heartsRulesConfig,
                                       @Header("username") String username) {
        logger.log("Creating game with ID " + gameId);
        logger.log("username for usee is:  " + username);
        HeartsService heartsService = (HeartsService) gameServiceFactory.createGameService(HEARTS, gameId, heartsRulesConfig);
        heartsService.addPlayer(new HumanPlayer(username, 0)); // Just assigning id 0 is okay here since its the first player
        gameRooms.put(gameId, heartsService);
        //TODO: add the player who created the room

        return new StartGameRequest(heartsRulesConfig);
    }

    @MessageMapping("/hearts/game-room/{gameId}/startGame")
    public GameStartMessage startGame(@DestinationVariable int gameId) {
        logger.log("Starting game with ID " + gameId);
        HeartsService heartsService = getGameService(gameId);
        heartsService.startGame();
        return new GameStartMessage(heartsService.getRulesConfig(),
                heartsService.getPlayers().stream().map(Player::getPlayerDescriptor).toList().toArray(new Player.PlayerDescriptor[0]));
    }

    //TODO: Does this need to be exposed? All joins should come through the GamesAPIController
    @Override
    @MessageMapping("/hearts/game-room/{gameId}/joinGame")
    public PlayerJoinedMessage joinGame(@DestinationVariable int gameId,
                                        @Payload Player.PlayerDescriptor playerJoined){
        Player playerToAdd = new HumanPlayer(playerJoined);
        getGameService(gameId).addPlayer(playerToAdd);
        return new PlayerJoinedMessage(playerToAdd, gameId);
    }

    @Override
    @MessageMapping("/hearts/game-room/{gameId}/playCard")
    public PlayCardMessage playCard(
            @DestinationVariable int gameId,
            @Payload PlayCardMessage cardMessage){
        // TODO: Need a way to rebroadcast full gamestate probably, because CPU plays wont broadcast a play message
        getGameService(gameId).playCard(cardMessage);
        return cardMessage;
    }

    @Override
    public List<GameService> getActiveGames() {
        return new ArrayList<>(gameRooms.values());
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


    @MessageMapping("/hearts/game-room/{gameId}/chat")
    public ChatMessage chatMessage(
            @DestinationVariable int gameId,
            @Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/hearts/game-room/{gameId}/activePlayers")
    public ActivePlayersMessage getActivePlayers(
            @DestinationVariable int gameId
    ) {
        HeartsService gameService = (HeartsService) gameRooms.get(gameId);
        return new ActivePlayersMessage(
                gameService.getPlayers().stream()
                        .map(Player::getPlayerDescriptor)
                        .collect(Collectors.toList()));
    }
    protected HeartsService getGameService(int gameId){
        HeartsService gameService = (HeartsService) gameRooms.get(gameId);
        if (gameService == null) {
            throw new IllegalArgumentException("Game ID " + gameId + " does not exist.");
        }
        return gameService;
    }

    @MessageExceptionHandler
    public void handleException(Exception exception) throws Exception{
        // TODO: Add exception handling
        throw exception;
        // ...
        //return appError;
    }


}
