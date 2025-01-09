package com.markndevon.cardgames.controller;

import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.message.*;
import com.markndevon.cardgames.model.Card;
import com.markndevon.cardgames.model.config.HeartsRulesConfig;
import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.player.Player;
import com.markndevon.cardgames.service.GameService;
import com.markndevon.cardgames.service.HeartsService;
import com.markndevon.cardgames.service.authentication.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    @Autowired
    private Logger logger;

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

    /**
     * Method called by a client joining a new game
     *
     * @param gameId game identification value
     * @return PlayerJoinedMessage with the descriptor of the player joining and the game identification value
     */
    @PostMapping("/games/joingame/{gameId}")
    public ResponseEntity<LobbyUpdateMessage> joinGame(@PathVariable int gameId,
                                                       @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtService.extractUsername(token);

        boolean gameHasPlayer = HEARTS_CONTROLLER.getGameService(gameId).getPlayers().stream().map(Player::getName).toList().contains(username);

        if(gameHasPlayer){
            GameService service = HEARTS_CONTROLLER.getGameService(gameId);
            LobbyUpdateMessage returnMessage =
                    new LobbyUpdateMessage(service.getPlayers(), service.getRulesConfig());

            return ResponseEntity.status(403).body(returnMessage);
        }
        // TODO: Should support other games here, not access HEARTS_CONTROLLER directly

        int playerId = HEARTS_CONTROLLER.getCurrentPlayerIdForGame(gameId);
        return ResponseEntity.ok(HEARTS_CONTROLLER.joinGame(gameId, new Player.PlayerDescriptor(username, playerId, true)));
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


    /**
     * Return a card image to the client based on the input
     *
     * @param deck This corresponds to the deck to use for display
     * @param card Card object that contains the actual rank and suit to be displayed
     * @return Response entity containing the image (or not)
     */
    @GetMapping("/games/images/card/{cardId}/{value}")
    public ResponseEntity<byte[]> getCardImage(@PathVariable String deck, @PathVariable Card card) {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            ImageIO.write(card.getImage(), "png", baos);
            byte[] serializedCard = baos.toByteArray();

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(serializedCard);
        } catch(IOException ex) {
            logger.error("Error writing card image out to byte stream", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        } catch(Exception ex) {
            logger.error("Unexpected error returning image", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
