package com.markndevon.cardgames.service;

import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.message.*;
import com.markndevon.cardgames.model.Card;
import com.markndevon.cardgames.model.config.HeartsRulesConfig;
import com.markndevon.cardgames.model.gamestates.HeartsGameState;
import com.markndevon.cardgames.model.player.HumanPlayer;
import com.markndevon.cardgames.model.player.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Map;

public class HeartsService extends GameService {
    HeartsGameState heartsGame;
    @Autowired
    SimpMessagingTemplate clientMessenger;

    @Autowired
    Logger logger;

    // TODO: Need to take in an initial playerID? Or just initialize a null list of players? GameState should probably track that
    public HeartsService(int gameId, HeartsRulesConfig rulesConfig){
        super(gameId, rulesConfig);
        heartsGame = new HeartsGameState(new Player[]{}, rulesConfig, logger);
        startGame();
    }
    public void playCard(PlayCardMessage playCard){
        heartsGame.playCard(new HumanPlayer(playCard.getPlayerDescriptor()), playCard.getCard());
        updateClients();
    }

    public void passCards(PassCardsMessage passCards){
        heartsGame.passCards(passCards.getPasser(), passCards.getReceiver(), passCards.getCards());
        updateClients();
    }

    public void startGame(){
        heartsGame.start();
        updateClients();
    }

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
                clientMessenger.convertAndSendToUser(player.getName(), "/topic/hearts/", legalPlaysMessage);
            }
        }
    }

    public void setLogger(Logger newLogger){
        logger = newLogger;
    }
}
