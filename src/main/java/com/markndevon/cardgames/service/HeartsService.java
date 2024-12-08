package com.markndevon.cardgames.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.message.*;
import com.markndevon.cardgames.model.config.HeartsRulesConfig;
import com.markndevon.cardgames.model.gamestates.HeartsGameState;
import com.markndevon.cardgames.model.player.HumanPlayer;
import com.markndevon.cardgames.model.player.Player;
import com.markndevon.cardgames.model.player.RandomAIPlayer;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Service object for managing a game of hearts
 */
public class HeartsService extends GameService {
    @JsonIgnore
    private SimpMessagingTemplate clientMessenger;
    @JsonIgnore
    private Logger logger;


    // TODO: Need to take in an initial playerID? Or just initialize a null list of players? GameState should probably track that
    public HeartsService(int gameId, HeartsRulesConfig rulesConfig, SimpMessagingTemplate clientMessenger, Logger logger){
        super(gameId, rulesConfig);
        this.clientMessenger = clientMessenger;
        this.logger = logger;

    }

    /**
     * Method triggered from the controller when a player is playing a card. Essentially just triggers the play card
     * message in the game state
     *
     * @param playCard message detailing card being played and by who
     */
    public void playCard(PlayCardMessage playCard){
        if(gameIsStarted){
            ((HeartsGameState) gameState).playCard(new HumanPlayer(playCard.getPlayerDescriptor()), playCard.getCard());
            updateClients();
        } else {
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
     *
     * TODO: Should probably communicate FULL game state instead of having a deal message/legal plays message
     */
    @Override
    public void startGame(){
        gameState = new HeartsGameState(possiblyFillPlayers(), (HeartsRulesConfig) rulesConfig, gameId, logger);
        gameState.start();
        gameIsStarted = true;
        //TODO: Do we need to broadcast a gamestart message?

        // When we start the game, need to deal the cards and init the kitty
        for(Player player : ((HeartsGameState) gameState).getPlayers()) {
            if(player.isHumanControlled()){
                DealMessage dealMessage = new DealMessage(player.getHand());
                UpdateLegalPlaysMessage legalPlaysMessage = new UpdateLegalPlaysMessage(((HeartsGameState) gameState).getLegalPlays(player));
                // TODO: I seriously doubt the player name is how spring will be storing this, need to figure that out
                // TODO: Repeated code section with updateClients

                System.out.println(player.getName());
                clientMessenger.convertAndSendToUser(player.getName(), "/hearts/game-room/" + gameId + "/deal", dealMessage);
                clientMessenger.convertAndSendToUser(player.getName(), "/hearts/game-room/" + gameId + "/legalPlays", legalPlaysMessage);
            }
        }

        updateClients();
    }

    /**
     * Method for updating the clients playing this particular game with the latest game state
     *
     * TODO: Actually communicate full game state instead of individual parts
     */
    @Override
    public void updateClients(){
        UpdateCurrentTrickMessage currentTrickMessage = new UpdateCurrentTrickMessage(((HeartsGameState) gameState).getCurrentTrickMap());
        clientMessenger.convertAndSend("/hearts/game-room/" + gameId + "/currentTrick", currentTrickMessage);

        UpdateScoreBoardMessage scoreBoardMessage = new UpdateScoreBoardMessage(((HeartsGameState) gameState).getScoreBoard().getScore());
        clientMessenger.convertAndSend("/hearts/game-room/" + gameId + "/updateScore", scoreBoardMessage);

        // Send the legal plays individually to each player
        for(Player player : ((HeartsGameState) gameState).getPlayers()) {
            if(player.isHumanControlled()){
                UpdateLegalPlaysMessage legalPlaysMessage = new UpdateLegalPlaysMessage(((HeartsGameState) gameState).getLegalPlays(player));
                // TODO: I seriously doubt the player name is how spring will be storing this, need to figure that out
                clientMessenger.convertAndSendToUser(player.getName(), "/hearts/game-room" + gameId + "/legalPlays", legalPlaysMessage);
            }
        }
    }

    /**
     * Utility method for filling out a gameState with CPU players
     *
     * @return player array updated with CPU players
     */
    private Player[] possiblyFillPlayers() {
        int size = getPlayers().size();

        for(int i = size; i < 4; i++) {
            addPlayer(new RandomAIPlayer("AI" + i, i));
        }

        System.out.println("Filling up game: " + getPlayers());

        return getPlayers().toArray(new Player[0]);
    }

}
