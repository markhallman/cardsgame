package com.markndevon.cardgames.model.player;

import com.markndevon.cardgames.model.Card;
import com.markndevon.cardgames.model.gamestates.GameState;
import com.markndevon.cardgames.model.util.Randomizer;

import java.util.List;

public class RandomAIPlayer extends Player {

    public RandomAIPlayer(String name, int id) {
        super(name, id, false);
    }

    @Override
    public Card getNextPlay(final GameState game) {
        final List<Card> legalPlays = hand.stream()
                .filter(card -> game.isLegal(this, card))
                .toList();
        if(legalPlays.isEmpty()) {
            throw new IllegalStateException("No legal plays for " + getName());
        }
        return Randomizer.INSTANCE.getRandomCard(legalPlays);
    }

    @Override
    public List<Card> getPassedCards() {
        return Randomizer.INSTANCE.getRandomCards(hand, 3);
    }
}
