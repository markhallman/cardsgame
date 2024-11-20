package com.markndevon.cardgames.service;

import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.message.*;
import com.markndevon.cardgames.model.config.HeartsRulesConfig;
import com.markndevon.cardgames.model.gamestates.HeartsGameState;
import com.markndevon.cardgames.model.player.HumanPlayer;
import com.markndevon.cardgames.model.player.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Scope("prototype")
public class HeartsService extends GameService {
    // TODO: Game state tacking should be in parent class
    private boolean gameIsStarted = false;
    private HeartsGameState heartsGame;
    @Autowired
    SimpMessagingTemplate clientMessenger;

    @Autowired
    Logger logger;

    // TODO: Need to take in an initial playerID? Or just initialize a null list of players? GameState should probably track that
    public HeartsService(int gameId, HeartsRulesConfig rulesConfig){
        super(gameId, rulesConfig);
    }
    public void playCard(PlayCardMessage playCard){
        if(gameIsStarted){
            heartsGame.playCard(new HumanPlayer(playCard.getPlayerDescriptor()), playCard.getCard());
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

    @Override
    public void startGame(){
        heartsGame = new HeartsGameState(getPlayers().toArray(new Player[getPlayers().size()]),(HeartsRulesConfig) rulesConfig, gameId, logger);
        heartsGame.start();
        gameIsStarted = true;
        //TODO: Do we need to broadcast a gamestart message?

        // When we start the game, need to deal the cards and init the kitty
        for(Player player : heartsGame.getPlayers()) {
            if(player.isHumanControlled()){
                DealMessage dealMessage = new DealMessage(player.getHand());
                UpdateLegalPlaysMessage legalPlaysMessage = new UpdateLegalPlaysMessage(heartsGame.getLegalPlays(player));
                // TODO: I seriously doubt the player name is how spring will be storing this, need to figure that out
                // TODO: Repeated code seciton with updateClients

                clientMessenger.convertAndSendToUser(player.getName(), "/topic/hearts/" + gameId, dealMessage);
                clientMessenger.convertAndSendToUser(player.getName(), "/topic/hearts/" + gameId, legalPlaysMessage);
            }
        }

        updateClients();
    }

    @Override
    public void updateClients(){
        UpdateCurrentTrickMessage currentTrickMessage = new UpdateCurrentTrickMessage(heartsGame.getCurrentTrickMap());
        clientMessenger.convertAndSend("/topic/hearts/" + gameId, currentTrickMessage);

        UpdateScoreBoardMessage scoreBoardMessage = new UpdateScoreBoardMessage(heartsGame.getScoreBoard().getScore());
        clientMessenger.convertAndSend("/topic/hearts/" + gameId, scoreBoardMessage);

        // Send the legal plays individually to each player
        for(Player player : heartsGame.getPlayers()) {
            if(player.isHumanControlled()){
                UpdateLegalPlaysMessage legalPlaysMessage = new UpdateLegalPlaysMessage(heartsGame.getLegalPlays(player));
                // TODO: I seriously doubt the player name is how spring will be storing this, need to figure that out
                clientMessenger.convertAndSendToUser(player.getName(), "/topic/hearts/" + gameId, legalPlaysMessage);
            }
        }
    }

    public void setLogger(Logger newLogger){
        logger = newLogger;
    }
}
