package com.markndevon.cardgames.model.player;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.model.Card;
import com.markndevon.cardgames.model.gamestates.GameState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public abstract class Player {
    private final Logger logger = Logger.getInstance();

    protected final List<Card> cardsWon = new ArrayList<>();
    protected List<Card> hand;
    private final String name;
    private final int id;

    private final boolean isHumanControlled;

    public Player(final String name, int id, boolean isHumanControlled) {
        this(new PlayerDescriptor(name, id, isHumanControlled));
    }
    public Player(final PlayerDescriptor playerDescriptor) {
        this.name = playerDescriptor.name;
        this.id = playerDescriptor.id;
        this.isHumanControlled = playerDescriptor.isHumanControlled;
    }

    public String getName(){ return name; }

    public int getId() {
        return id;
    }

    public void setHand(final List<Card> hand) {
        this.hand = hand;
        logger.log(toDetailedString());
    }
    public List<Card> getHand() {
        return hand;
    }

    public boolean isHumanControlled(){
        return isHumanControlled;
    }
    public void removeCard(final Card card) {
        hand.remove(card);
        logger.log("Player.removeCard: " + toDetailedString());
    }
    public void removeCards(final Collection<Card> cards) {
        hand.removeAll(cards);
        logger.log("Player.removeCards: " + toDetailedString());
    }
    public void addCards(final List<Card> cards) {
        hand.addAll(cards);
        logger.log("Player.addCards: " + toDetailedString());
    }

    public void addCardsWon(final List<Card> trick) {
        this.cardsWon.addAll(trick);
    }

    public void clearTricksWon() {
        this.cardsWon.clear();
    }
    public List<Card> getCardsWon() {
        return cardsWon;
    }

    public abstract Card getNextPlay(final GameState gameState);

    public PlayerDescriptor getPlayerDescriptor() {
        return new PlayerDescriptor(name, id, isHumanControlled); //todo this is stupid
    }

    @JsonIgnore
    public List<Card> getPassedCards() {
        throw new UnsupportedOperationException();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }

    public String toDetailedString() {
        return getPlayerDescriptor().toString() + ": " + hand.toString();
    }

    public record PlayerDescriptor(String name, int id, boolean isHumanControlled) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PlayerDescriptor player = (PlayerDescriptor) o;
            return id == player.id;
        }

        @Override
        public String toString() {
            return "Player --- name: " + this.name + " ID: " + this.id + " isHumanControlled: " + this.isHumanControlled;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
