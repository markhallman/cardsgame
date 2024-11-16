package com.markndevon.cardgames.model.util;

import com.markndevon.cardgames.model.Card;
import com.markndevon.cardgames.model.player.Player;

import java.util.*;

public class Util {

    /**
     * Evenly distributes a shuffled deck of cards among the Players
     * @return the leftover cards
     */
    public static List<Card> dealCardsAndReturnExtra(final Player[] players) {
        final List<Card> cards = Card.FullDeck.getShuffledDeck();

        final int numCardsPerPlayer = cards.size() / players.length;

        for(int i = 0; i < players.length; i++) {
            final List<Card> hand = new ArrayList<>();
            for(int j = 0; j < numCardsPerPlayer; j++) {
                hand.add(cards.get((i * numCardsPerPlayer) + j));
            }
            players[i].setHand(hand);
        }

        final List<Card> extra = new ArrayList<>();
        for(int i = players.length * numCardsPerPlayer; i < cards.size(); i++) {
            extra.add(cards.get(i));
        }

        return extra;
    }

    /**
     * @return the index of the highest value card of the lead suit
     */
    public static int getHighCard(final List<Card> cards) {
        if(cards.isEmpty()) {
            return -1;
        }
        return getHighCard(cards, cards.get(0).getSuit());
    }
    /**
     * @return index of the highest value card of the given suit. -1 if there is no such card
     */
    public static int getHighCard(final List<Card> cards, final Card.Suit suit) {
        if(cards.isEmpty()) {
            return -1;
        }

        Card max = null;
        int highCard = -1;
        for(int i = 0; i < cards.size(); i++) {
            final Card card = cards.get(i);
            if(card.getSuit().equals(suit)) {
                if(max == null || max.getValue().getVal() < card.getValue().getVal()) {
                    max = card;
                    highCard = i;
                }
            }
        }

        return highCard;
    }

    public static boolean hasSuit(final List<Card> cards, final Card.Suit suit) {
        return cards.stream().anyMatch(card -> card.getSuit() == suit);
    }

    public static String convertIndexToPlacement(final int i) {
        switch(i) {
            case 0 -> { return "first"; }
            case 1 -> { return "second"; }
            case 2 -> { return "third"; }
            case 3 -> { return "fourth"; }
            case 4 -> { return "fifth"; }
            case 5 -> { return "sixth"; }
            case 6 -> { return "seventh"; }
            case 7 -> { return "eighth"; }
            default -> throw new IllegalArgumentException("Index " + i + " is too high to convert to a placement");
        }
    }

    public static List<Player> determinePlacements(final Map<Player, Integer> score, boolean lowWins) {
        final List<Player> players = new ArrayList<>(score.keySet());

        if(lowWins) {
            players.sort(Comparator.comparingInt(score::get));
        } else {
            players.sort((o1, o2) -> -1 * Integer.compare(score.get(o1), score.get(o2)));
        }

        return players;
    }

    public static List<Card> getSortedCopy(final List<Card> cards) {
        if(cards == null || cards.isEmpty()) {
            return new ArrayList<>();
        }

        final List<Card> sorted = new ArrayList<>(cards);
        sorted.sort(new Standard52CardComparator());
        return sorted;
    }

    public static class Standard52CardComparator implements Comparator<Card> {

        @Override
        public int compare(Card o1, Card o2) {
            if(o1.getSuit().equals(o2.getSuit())) {
                return Integer.compare(o1.getValue().getVal(), o2.getValue().getVal());
            }

            return o1.getSuit().bridgeStyleCompareTo(o2.getSuit());
        }
    }
}
