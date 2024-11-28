package com.markndevon.cardgames.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markndevon.cardgames.model.util.Randomizer;
import com.markndevon.cardgames.model.util.ResourceManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Card {

    public enum Suit {
        DIAMOND,
        CLUB,
        HEART,
        SPADE;

        public int bridgeStyleCompareTo(Suit suit) {
            return Integer.compare(this.ordinal(), suit.ordinal());
        }
    }

    public enum Value {
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9),
        TEN(10),
        JACK(11),
        QUEEN(12),
        KING(13),
        ACE(14);
        private final int val;
        Value(final int val) {
            this.val = val;
        }
        public int getVal() {
            return val;
        }
    }

    private final Suit suit;
    private final Value value;
    @JsonIgnore
    private final Image image;

    @JsonIgnore
    private final Image rotatedImage;
    public Card(final Suit suit, final Value value) {
        this.suit = suit;
        this.value = value;
        this.image = ResourceManager.getCardImage(this); // TODO: Figure out resource management in spring
        this.rotatedImage = ResourceManager.getCardImage(this, true);
    }

    public Suit getSuit() {
        return suit;
    }

    public Value getValue() {
        return value;
    }

    public int getRank() {
        return value.getVal();
    }

    public Image getImage() {
        return image;
    }
    public Image getImage(final boolean rotate) {
        return rotate ? rotatedImage : image;
    }

    @Override
    public boolean equals(final Object o) {
        if(o instanceof Card) {
            return ((Card) o).suit == this.suit && ((Card) o).value == this.value;
        }

        return false;
    }

    @Override
    public String toString() {
        return value.name() + " " + suit.name();
    }

    @Override
    public int hashCode() {
        return 31 * suit.hashCode() + value.hashCode();
    }

    public static class FullDeck {
        private final List<Card> cards = new ArrayList<>();
        public FullDeck() {
            for(final Suit suit : Suit.values()) {
                for(final Value value : Value.values()) {
                    cards.add(new Card(suit, value));
                }
            }
        }

        public List<Card> getCards() {
            return cards;
        }

        public static List<Card> getShuffledDeck() {
            final List<Card> cards = new FullDeck().getCards();
            Randomizer.INSTANCE.inPlaceShuffle(cards);
            return cards;
        }
    }
}
