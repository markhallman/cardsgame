package com.markndevon.cardgames.controller;

import com.markndevon.cardgames.model.config.HeartsRulesConfig;
import com.markndevon.cardgames.model.config.RulesConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Universal game manager. Keeps track of all active games and assigns Game IDs for tracking
 */
@RestController
public class GamesAPIController {

    @Autowired
    private HeartsController HEARTS_CONTROLLER;
    // TODO: initial value should be grabbed from database
    // TODO: active games details should be stored in a database so we can retrieve them even if service crashes
    private static AtomicInteger GAME_ID_CREATOR = new AtomicInteger(1000);

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }

    /**
     * Method for creating a new game
     * @param gameType the name of the game that is being played
     * @return the game ID of the newly created game
     */
    //TODO: A PLAYER name and/or ID should be passed along too
    //TODO: Will a heartsRulesConfig properly parse here when its expecting its parent interface?
    @PostMapping("/games/creategame/{gameType}")
    public int createGame(@PathVariable String gameType, @RequestBody(required = false) RulesConfig rulesConfig) {
        int gameID = GAME_ID_CREATOR.incrementAndGet();

        if (gameType.equalsIgnoreCase("HEARTS")){
            if(rulesConfig == null){
                // Get default by building without setting any rules
                rulesConfig = (new HeartsRulesConfig.Builder()).build();
            }
            HEARTS_CONTROLLER.createGame(GAME_ID_CREATOR.incrementAndGet(), rulesConfig);
        } else {
            throw new IllegalArgumentException("Game Type " + gameType + " currently not supported");
        }

        return gameID;
    }

    // TODO: actually return game list?
    @GetMapping("/games/activegames")
    public List<GameController> getActiveGames(){
        List<GameController> controllers = new ArrayList<>();
        controllers.add(HEARTS_CONTROLLER);
        return controllers;
    }


}
