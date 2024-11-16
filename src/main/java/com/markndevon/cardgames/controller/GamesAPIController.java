package com.markndevon.cardgames.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Universal game manager. Keeps track of all active games and assigns Game IDs for tracking
 */
@RestController
public class GamesAPIController {

    @Autowired
    private static HeartsController HEARTS_CONTROLLER;
    // TODO: initial value should be grabbed from database
    // TODO: active games details should be stored in a database so we can retrieve them even if service crashes
    private static AtomicInteger GAME_ID_CREATOR = new AtomicInteger(1000);
    private static List<GameController> CONTROLLERS = new ArrayList<>();
    static {
        CONTROLLERS.add(HEARTS_CONTROLLER);
    }

    /**
     * Method for creating a new game
     * @param gameType the name of the game that is being played
     * @return the game ID of the newly created game
     */
    //TODO: A PLAYER name and/or ID should be passed along too
    @PostMapping("/games/creategame/{gameType}")
    public int createGame(String gameType){
        int gameID = GAME_ID_CREATOR.incrementAndGet();

        if (gameType.equalsIgnoreCase("HEARTS")){
            HEARTS_CONTROLLER.createGame(GAME_ID_CREATOR.incrementAndGet());
        } else {
            throw new IllegalArgumentException("Game Type " + gameType + " currently not supported");
        }

        return gameID;
    }

    // TODO: actually return game list?
    @GetMapping("/games/activegames")
    public List<GameController> getActiveGames(){
        return CONTROLLERS;
    }


}
