package com.markndevon.cardgames.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.message.GameUpdateMessage;
import com.markndevon.cardgames.message.PlayCardMessage;
import com.markndevon.cardgames.model.config.HeartsRulesConfig;
import com.markndevon.cardgames.model.gamestates.HeartsGameState;
import com.markndevon.cardgames.model.player.HumanPlayer;
import com.markndevon.cardgames.model.player.Player;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Service object for managing a game of hearts
 */
public class HeartsService extends GameService {
    @JsonIgnore
    private SimpMessagingTemplate clientMessenger;
    @JsonIgnore
    private Logger logger;
    @JsonIgnore
    private final static long CPU_TURN_SLEEP_TIME = 1000L;

    public HeartsService(int gameId, HeartsRulesConfig rulesConfig, HumanPlayer gameOwner, SimpMessagingTemplate clientMessenger, Logger logger){
        super(gameId, rulesConfig, gameOwner);
        this.clientMessenger = clientMessenger;
        this.logger = logger;
    }

    /**
     * Method triggered from the controller when a player is playing a card. Essentially just triggers the play card
     * message in the game state
     *
     * @param playCard message detailing card being played and by who
     */
    public synchronized void playCard(PlayCardMessage playCard){
        if(gameIsStarted){
            HumanPlayer playingPlayer = getPlayers()
                    .stream()
                    .filter(player -> player.getName().equals(playCard.getPlayerName()))
                    .map(player -> (HumanPlayer) player).findFirst().orElse(null);

            if(playingPlayer == null){
                throw new RuntimeException("Player " + playCard.getPlayerName() + " not found in game");
            }

            HeartsGameState currGameState = (HeartsGameState) gameState;

            // TODO: Refactor some repeat code here?
            if(currGameState.playCard(playingPlayer, playCard.getCard())){
                updateClients();

                try {
                    Thread.sleep(CPU_TURN_SLEEP_TIME);
                } catch (InterruptedException e) {
                    logger.error("Thread interrupted while sleeping for CPU turn", e);
                }

                ((HeartsGameState)getGameState()).possiblyResolveTrick();
                ((HeartsGameState)getGameState()).possiblyResolveHand();
                updateClients(); // Should send an updated game state after hand/trick resolution as well

                playCPUTurns();
            }
        } else {
            logger.log("Game not started, cannot play card");
            // TODO: error handling
        }

    }

/*
    public void passCards(PassCardsMessage passCards){
        heartsGame.passCards(passCards.getPasser(), passCards.getReceiver(), passCards.getCards());
        updateClients();
    }
*/

    /**
     * Service method for actually starting the game of hearts. Fills in any unoccupied spots in the game with
     * Computer players, triggers teh start method of the GameState, and communicates initial game state to all
     * the clients
     **/
    @Override
    public void startGame(){
        gameState = new HeartsGameState(possiblyFillPlayers(), (HeartsRulesConfig) rulesConfig, gameId, logger);
        gameState.start();
        gameIsStarted = true;

        logger.log("Broadcasting starting state");

        updateClients();

        playCPUTurns();
    }

    /**
     * Convenience method for playing CPU turns in the game
     *
     * // TODO: Some repeated code with the playCard method, should probably refactor
     */
    private void playCPUTurns(){
        // If the next player is a CPU, resolve their action as well
        HeartsGameState currGameState = (HeartsGameState) gameState;

        Player currPlayer = currGameState.getCurrentPlayer();
        while(!currPlayer.isHumanControlled()){
            currGameState.playCard(currPlayer, currPlayer.getNextPlay(getGameState()));
            updateClients();
            try {
                Thread.sleep(CPU_TURN_SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // This should be controlled from the service, so the game has a chance to update before trick is resovled
            ((HeartsGameState)getGameState()).possiblyResolveTrick();
            ((HeartsGameState)getGameState()).possiblyResolveHand();
            System.out.println("Current Player: " + ((HeartsGameState) getGameState()).getCurrentPlayer());
            updateClients(); // Should send an updated game state after hand/trick resolution as well

            currPlayer = ((HeartsGameState)getGameState()).getCurrentPlayer();
        }
    }

    /**
     * Method for updating the clients playing this particular game with the latest game state
     *
     * TODO: so users cant see other users hands, we will NEED to filter the game state somehow
     *  at some point and send separate messages
     */
    @Override
    public void updateClients(){
        logger.log("Broadcasting current game state to all clients");
        GameUpdateMessage currGameStateMessage = new GameUpdateMessage(gameState);
        clientMessenger.convertAndSend("/topic/hearts/game-room/" + gameId + "", currGameStateMessage);
    }

}
