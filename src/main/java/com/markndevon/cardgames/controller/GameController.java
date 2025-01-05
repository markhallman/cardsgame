package com.markndevon.cardgames.controller;

import com.markndevon.cardgames.message.*;
import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.player.Player;
import com.markndevon.cardgames.service.GameService;
import com.markndevon.cardgames.service.HeartsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/*
    Generic interface for game controller, handles communication from the client to manage the game state
 */
public abstract class GameController {
    protected final Map<Integer, GameService> gameRooms = new ConcurrentHashMap<>();

    public abstract StartGameRequest createGame(int gameId, RulesConfig rulesConfig, String username);
    public abstract LobbyUpdateMessage joinGame(int gameId, Player.PlayerDescriptor player);
    public abstract LobbyUpdateMessage leaveGame(int gameId, Player.PlayerDescriptor player);
    public abstract LobbyUpdateMessage updateRules(int gameId, RulesConfig rulesConfig);
    public abstract GameStartMessage startGame(int gameId);
    public abstract GameUpdateMessage playCard(int gameId, PlayCardMessage cardMessage, String username) throws IllegalAccessException;

    /**
     * Get the HeartsService associated with a given gameId. Each GameState has a separate
     * GameService object to manage it
     *
     * @param gameId game identification value
     * @return The HeartsService object associated with the given ID
     */
    public GameService getGameService(int gameId){
        GameService gameService = gameRooms.get(gameId);
        if (gameService == null) {
            throw new IllegalArgumentException("Game ID " + gameId + " does not exist.");
        }
        return gameService;
    }

    /**
     * Get a list of active games being  managed by this controller
     *
     * @return List of GameService objects being managed by the controller
     */
    public List<GameService> getActiveGames() {
        return new ArrayList<>(gameRooms.values());
    }

    public int getCurrentPlayerIdForGame(int gameId) {
        // TODO: synchronization issues? Might want to use a atomic integer to track id
        // TODO: Return -1 if the game is full.
        return gameRooms.get(gameId).getPlayers().size();
    }


    /**
     * Kick a user from any games they are currently participating in
     *
     * TODO: Probably also need to signal the client
     *  so that they can be navigated away from the page (if they are still on it)
     *
     * @param username name of the user to kick
     */
    public void kickUser(String username) {
        System.out.println("Im kicking a user from the game!!!");

        // TODO: Worst possible way to check this, but wont matter unless we have more a ton of games going on
        //      which seems unlikely.
        //      Should probably hash the gameService by username or something for quick access
        for (GameService gameRoom : gameRooms.values()){
            Optional<Player> maybePlayer = gameRoom.getPlayers().stream().filter(player -> player.getName().equals(username)).findFirst();
            if(maybePlayer.isPresent()){
                Player playerToKick = maybePlayer.get();
                leaveGame(gameRoom.getGameId(), playerToKick.getPlayerDescriptor());
            }
        }
    }
}
