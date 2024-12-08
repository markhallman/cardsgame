package com.markndevon.cardgames.controller;

import com.markndevon.cardgames.message.ActiveGamesMessage;
import com.markndevon.cardgames.message.CreateGameMessage;
import com.markndevon.cardgames.message.PlayerJoinedMessage;
import com.markndevon.cardgames.model.config.HeartsRulesConfig;
import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.player.Player;
import org.springframework.beans.factory.annotation.Autowired;
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
    // TODO: initial value should be grabbed from database
    // TODO: active games details should be stored in a database so we can retrieve them even if service crashes
    private static final AtomicInteger GAME_ID_CREATOR = new AtomicInteger(1000);

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }

    /**
     * Method for creating a new game
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

    @PostMapping("/games/joingame/{gameId}")
    public PlayerJoinedMessage joinGame(@PathVariable int gameId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "anonymousUser";

        //TODO: If the user is already in the game, we should probably reject it

        int playerId = HEARTS_CONTROLLER.getCurrentPlayerIdForGame(gameId);
        return HEARTS_CONTROLLER.joinGame(gameId, new Player.PlayerDescriptor(username, playerId, true));
    }

    @PostMapping("/games/startgame/{gameId}")


    @GetMapping("/games/activegames")
    public ActiveGamesMessage getActiveGames() {
        List<GameController> controllers = new ArrayList<>();
        controllers.add(HEARTS_CONTROLLER);
        return new ActiveGamesMessage(controllers
                .stream()
                .flatMap(controller -> controller.getActiveGames().stream())
                .collect(Collectors.toList()));
    }
}
