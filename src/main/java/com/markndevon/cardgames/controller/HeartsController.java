package com.markndevon.cardgames.controller;

import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.message.*;
import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.gamestates.HeartsGameState;
import com.markndevon.cardgames.model.player.HumanPlayer;
import com.markndevon.cardgames.model.player.Player;
import com.markndevon.cardgames.service.GameService;
import com.markndevon.cardgames.service.GameServiceFactory;
import com.markndevon.cardgames.service.HeartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.markndevon.cardgames.model.gamestates.GameType.HEARTS;

/**
 * Controller class for a game of hearts
 *
 * //TODO: We should move whatever we can to the parent GameController class so logic can be reused
 */
@Controller
public class HeartsController extends GameController {

    //TODO: Make sure game removals are handled correctly. Scheduled task to remove inactive games?
    @Autowired
    private Logger logger;

    private final GameServiceFactory gameServiceFactory;

    @Autowired
    private SimpMessagingTemplate clientMessenger;

    public HeartsController(GameServiceFactory gameServiceFactory) {
        this.gameServiceFactory = gameServiceFactory;
    }

    /**
     * This method is called by the controller when a client submits a REST API reuest to start a game
     * return value is broadcasted to all listening clients
     *
     * @param gameId game identification value
     * @param heartsRulesConfig rules configuration for the game being created.
     * @param username username of the user creating the game. Should be given control by default
     * @return StartGameRequest containing the initial set of rules the game was created with. Subject to change
     */
    @Override
    @SendTo("/topic/hearts/game-room")
    public StartGameRequest createGame(int gameId,
                                       RulesConfig heartsRulesConfig,
                                       @Header("username") String username) {
        logger.log("Creating game with ID " + gameId);
        logger.log("username for user is:  " + username);
        HeartsService heartsService = (HeartsService) gameServiceFactory.createGameService(HEARTS, gameId, heartsRulesConfig);
        heartsService.addPlayer(new HumanPlayer(username, 0)); // Just assigning id 0 is okay here since its the first player
        gameRooms.put(gameId, heartsService);
        //TODO: add the player who created the room

        return new StartGameRequest(heartsRulesConfig);
    }

    /**
     * Method called by a client from the lobby when they want to actually start the game, idetified through gameId in
     * the message path
     *
     * @param gameId game identification value
     * @return GameStartMessage indicating the rules for the game and a list of players participating
     */
    @MessageMapping("/hearts/game-room/{gameId}/startGame")
    public GameStartMessage startGame(@DestinationVariable int gameId) {
        logger.log("Starting game with ID " + gameId);
        HeartsService heartsService = getGameService(gameId);
        heartsService.startGame();
        return new GameStartMessage(heartsService.getRulesConfig(),
                heartsService.getPlayers().stream().map(Player::getPlayerDescriptor).toList().toArray(new Player.PlayerDescriptor[0]));
    }

    /**
     * Method called by a client from the list of active games when they wish to join a game
     * CURRENTLY NO WAY TO PASSWORD PROTECT A GAME, ANYONE CAN JOIN ANY GAME
     *
     * TODO: Fix this
     *
     * @param gameId game identification value
     * @param playerJoined player descriptor of the joining player, to be added to the game state
     * @return PlayerJoinedMessage with the details of the joining player and the gameId of the game they have joined
     */
    @Override
    public LobbyUpdateMessage joinGame(@DestinationVariable int gameId,
                                        @Payload Player.PlayerDescriptor playerJoined) {
        Player playerToAdd = new HumanPlayer(playerJoined);
        HeartsService heartsService = getGameService(gameId);
        heartsService.addPlayer(playerToAdd);

        LobbyUpdateMessage playerAddedMessage =
                new LobbyUpdateMessage(heartsService.getPlayers(), heartsService.getRulesConfig());

        // TODO: Should we be sendin this from the API controller or here? Does it matter?
        clientMessenger.convertAndSend("/topic/hearts/game-lobby/" + gameId, playerAddedMessage );

        return new LobbyUpdateMessage(heartsService.getPlayers(), heartsService.getRulesConfig());
    }

    @Override
    @MessageMapping("/hearts/game-lobby/{gameId}")
    public LobbyUpdateMessage leaveGame(@DestinationVariable int gameId,
                                        @Payload Player.PlayerDescriptor playerLeave) {
        Player playerToRemove = new HumanPlayer(playerLeave);
        HeartsService heartsService = getGameService(gameId);
        heartsService.removePlayer(playerToRemove);
        return new LobbyUpdateMessage(heartsService.getPlayers(), heartsService.getRulesConfig());
    }

    @Override
    @MessageMapping("/hearts/game-room/{gameId}/updateRules")
    public LobbyUpdateMessage updateRules(int gameId, RulesConfig rulesConfig) {
        HeartsService heartsService = getGameService(gameId);
        heartsService.setRulesConfig(rulesConfig);

        return new LobbyUpdateMessage(heartsService.getPlayers(), heartsService.getRulesConfig());
    }

    /**
     * Method called by a client playing a card in a game of hearts.
     *
     * @param gameId game identification value
     * @param cardMessage  PlayCardMessage identifying the card being played and the player who is playing it
     * @return PlayCardMessage identifying the card played and the player who played it
     */
    @Override
    @MessageMapping("/hearts/game-room/{gameId}/playCard")
    public GameUpdateMessage playCard(
            @DestinationVariable int gameId,
            @Payload PlayCardMessage cardMessage,
            @Header("user-name") String username) throws IllegalAccessException {
        logger.log("FUllMessage: " + cardMessage.toString());
        logger.log("PlayCard message received for game " + gameId + " from player " + username + " with card " + cardMessage.getCard());
        logger.log("PlayCard other stuff " + cardMessage.getPlayerName() + " " + cardMessage.getMessageType());

        HeartsService heartsService = getGameService(gameId);
        HeartsGameState currGameSate = (HeartsGameState) heartsService.getGameState();

        // TODO: Not sure if this is the ideal way to validate a user, username in header can be faked.
        if (currGameSate.getCurrentPlayer().getName().equals(username)) {
            heartsService.playCard(cardMessage);
            return new GameUpdateMessage(heartsService.getGameState());
        }

        throw new IllegalAccessException("It is not the turn of player " + username);
    }

    /**
     * Get a list of active games being  managed by this controller
     *
     * @return List of GameService objects being managed by the controller
     */
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


    /**
     * Method called by the client when sending a chat message. Rebroadcast to all clients listeing on the channel
     *
     * @param gameId game identification value
     * @param chatMessage message detailing the message contents and the message sender
     * @return
     */
    @MessageMapping("/hearts/game-room/{gameId}/chat")
    public ChatMessage chatMessage(
            @DestinationVariable int gameId,
            @Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    /**
     * Method allowing a client to request the current set of active players in a game
     *
     * @param gameId game identification value
     * @return ActivePlayersMessage with a list of active game players
     */
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

    /**
     * Method for getting the current state of a hearts game based on the game identification code
     *
     * @return GameUpdateMessage represting the current full state of the game
     */
    @MessageMapping("/hearts/game-room/{gameId}/gameState")
    public GameUpdateMessage getGameState(@DestinationVariable int gameId){
        System.out.println("Sending out updated game state for game " + gameId);
        return new GameUpdateMessage(gameRooms.get(gameId).getGameState());
    }

    /**
     * Get the HeartsService associated with a given gameId. Each GameState has a separate
     * GameService object to manage it
     *
     * @param gameId game identification value
     * @return The HeartsService object associated with the given ID
     */
    public HeartsService getGameService(int gameId){
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
