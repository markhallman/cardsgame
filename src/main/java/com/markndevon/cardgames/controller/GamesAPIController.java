package com.markndevon.cardgames.controller;

import com.markndevon.cardgames.message.*;
import com.markndevon.cardgames.model.config.HeartsRulesConfig;
import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.player.Player;
import com.markndevon.cardgames.service.GameService;
import com.markndevon.cardgames.service.HeartsService;
import com.markndevon.cardgames.service.authentication.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Universal game manager. Keeps track of all active games and assigns Game IDs for tracking
 */
@RestController
public class GamesAPIController {

    @Autowired
    private HeartsController HEARTS_CONTROLLER;

    @Autowired
    private JWTService jwtService;

    // TODO: initial value should be grabbed from database (we need to add game state persistence to DB)
    // TODO: active games details should be stored in a database so we can retrieve them even if service crashes
    private static final AtomicInteger GAME_ID_CREATOR = new AtomicInteger(1000);

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }

    /**
     * Method for creating a new game
     *
     * @param gameType the name of the game that is being played
     * @return the game ID of the newly created game
     */
    @PostMapping("/games/creategame/{gameType}")
    public int createGame(@PathVariable String gameType,
                          @RequestBody CreateGameMessage createGameMessage) {
        int gameID = GAME_ID_CREATOR.incrementAndGet();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "anonymousUser";

        RulesConfig rulesConfig = createGameMessage.getRulesConfig();

        if (gameType.equalsIgnoreCase("HEARTS")){
            if(rulesConfig == null){
                // Get default by building without setting any rules
                rulesConfig = (new HeartsRulesConfig.HeartsBuilder()).build();
            }
            HEARTS_CONTROLLER.createGame(gameID, rulesConfig, username);
        } else {
            throw new IllegalArgumentException("Game Type " + gameType + " currently not supported");
        }

        return gameID;
    }

    @PostMapping("games/startgame/{gameId}")
    public GameStartMessage startGame(@PathVariable int gameId){
        // TODO Do some user authentication to make sure this user is allowed to start this game
        return HEARTS_CONTROLLER.startGame(gameId);
    }

    /**
     * Method called by a client joining a new game
     *
     * @param gameId game identification value
     * @return PlayerJoinedMessage with the descriptor of the player joining and the game identification value
     */
    @PostMapping("/games/joingame/{gameId}")
    public LobbyUpdateMessage joinGame(@PathVariable int gameId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "anonymousUser";

        //TODO: If the user is already in the game, we should probably reject it
        //HEARTS_CONTROLLER.getGameService(gameId).getPlayers()
        // TODO: Should support other games here

        int playerId = HEARTS_CONTROLLER.getCurrentPlayerIdForGame(gameId);
        return HEARTS_CONTROLLER.joinGame(gameId, new Player.PlayerDescriptor(username, playerId, true));
    }

    /**
     * API method for getting a list of ALL currently active games. Goes through all the different controller beans
     * and returns each of the games they are managing
     *
     * @return message detailing the list of currently active games
     */
    @GetMapping("/games/activegames")
    public ActiveGamesMessage getActiveGames() {
        List<GameController> controllers = new ArrayList<>();
        controllers.add(HEARTS_CONTROLLER);
        return new ActiveGamesMessage(controllers
                .stream()
                .flatMap(controller -> controller.getActiveGames().stream())
                .collect(Collectors.toList()));
    }

    @GetMapping("/games/authenticated/{gameId}")
    public ResponseEntity<Boolean> userIsGameMember(@PathVariable int gameId,
                                                @RequestHeader("Authorization") String authHeader){
        String token = authHeader.replace("Bearer ", "");
        String username = jwtService.extractUsername(token);
        GameService heartsService = HEARTS_CONTROLLER.getGameService(gameId);

        boolean isAuthorized =
                heartsService.getPlayers().stream().map(Player::getName).toList().contains(username);

        return ResponseEntity.ok(isAuthorized);
    }

    @GetMapping("/games/isStarted/{gameId}")
    public ResponseEntity<Boolean> gameIsStarted(@PathVariable int gameId){

        boolean gameIsStarted = HEARTS_CONTROLLER.getGameService(gameId).getGameIsStarted();
        return ResponseEntity.ok(gameIsStarted);
    }
}
