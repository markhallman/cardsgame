package com.markndevon.cardgames.model.util;

import com.markndevon.cardgames.model.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Randomizer {

    private final Random randy = new Random();
    public static final Randomizer INSTANCE = new Randomizer();

    private Randomizer() {

    }

    /**
     * In place shuffle of cards
     */
    public void inPlaceShuffle(final List<Card> cards) {
        Collections.shuffle(cards);
    }

    public Card getRandomCard(final List<Card> options) {
        final int r = randy.nextInt(options.size());
        return options.get(r);
    }

    public List<Card> getRandomCards(final List<Card> options, final int num) {
        final List<Card> cards = new ArrayList<>(options);
        inPlaceShuffle(cards);
        return cards.subList(0, num);
    }
    public int getRandomLobbyID() {
        return randy.nextInt(10); //todo network
    }
}
