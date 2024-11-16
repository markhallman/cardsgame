package com.markndevon.cardgames.service;

import com.markndevon.cardgames.message.LastTrickPlayedMessage;
import com.markndevon.cardgames.message.PassCardsMessage;
import com.markndevon.cardgames.message.PlayCardMessage;
import com.markndevon.cardgames.model.Card;
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

    public HeartsService(int gameId){
        super(gameId);
    }
    public void playCard(PlayCardMessage playCard){
        heartsGame.playCard(new HumanPlayer(playCard.getPlayerDescriptor()), playCard.getCard());
    }

    public void passCards(PassCardsMessage passCards){
        heartsGame.passCards(passCards.getPasser(), passCards.getReceiver(), passCards.getCards());
    }

    public void dealCards(int gameId){
        clientMessenger.convertAndSend("");
    }

    public void possilbyResolveTrick(final Map<Player.PlayerDescriptor, Card> trick){
        LastTrickPlayedMessage lastTrick = new LastTrickPlayedMessage(trick);
        clientMessenger.convertAndSend("/topic/hearts/", lastTrick);
    }
}
