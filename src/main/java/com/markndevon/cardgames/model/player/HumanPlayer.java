package com.markndevon.cardgames.model.player;

import com.markndevon.cardgames.model.Card;
import com.markndevon.cardgames.model.gamestates.GameState;


public class HumanPlayer extends Player {

    private Card selectedCard = null;
    private Card cardToPlay = null;
    private final Object cardMutex = new Object();

    public HumanPlayer(PlayerDescriptor descriptor){
        super(descriptor);
    }

    public HumanPlayer(String name, int id) {
        super(name, id, true);
    }

    @Override
    public Card getNextPlay(GameState game) {
        synchronized (cardMutex) {
            try {
                cardMutex.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            final Card returnCard = cardToPlay;
            cardToPlay = null;
            selectedCard = null;
            return returnCard;
        }
    }
}
