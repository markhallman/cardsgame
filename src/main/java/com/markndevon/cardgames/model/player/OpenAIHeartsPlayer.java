package com.markndevon.cardgames.model.player;

import com.markndevon.cardgames.model.Card;
import com.markndevon.cardgames.model.gamestates.GameState;
import com.markndevon.cardgames.model.gamestates.HeartsGameState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OpenAIHeartsPlayer extends Player {

    public OpenAIHeartsPlayer(PlayerDescriptor descriptor){
        super(descriptor);
    }

    public OpenAIHeartsPlayer(String name, int id) {
        super(name, id, false);
    }

    @Override
    public Card getNextPlay(final GameState gameState) {

        HeartsGameState heartsGameState = (HeartsGameState) gameState;
        final List<Card> currentTrick = heartsGameState.getCurrentTrick();

        if (currentTrick.isEmpty()) {
            return playLeadCard(heartsGameState);
        } else {
            Card.Suit leadingSuit = currentTrick.get(0).getSuit();
            boolean jackOfDiamondsInTrick = currentTrick.stream().anyMatch(HeartsGameState::isJackOfDiamonds);

            List<Card> sameSuitCards = hand.stream()
                    .filter(card -> card.getSuit() == leadingSuit)
                    .collect(Collectors.toList());

            if (!sameSuitCards.isEmpty()) {
                if (jackOfDiamondsInTrick) {
                    return playHighestCard(sameSuitCards);
                } else {
                    return playLowestCard(sameSuitCards);
                }
            } else {
                if (jackOfDiamondsInTrick) {
                    return playHighCard();
                } else {
                    return playLowestCard(hand);
                }
            }
        }
    }

    private boolean evaluateShootMoon() {
        // Basic logic to evaluate if shooting the moon is viable.
        long highCardsCount = hand.stream()
                .filter(card -> card.getValue().getVal() >= 10 || HeartsGameState.isQueenOfSpades(card))
                .count();

        long heartsCount = hand.stream()
                .filter(HeartsGameState::isHeart)
                .count();

        return highCardsCount >= 6 && heartsCount >= 5;
    }

    private boolean shouldGiveUpShootMoon() {
        // Logic to decide if shooting the moon should be abandoned
        // For example, if too many hearts or the Queen of Spades have already been played and not taken by the CPU
        long heartsInHand = hand.stream()
                .filter(HeartsGameState::isHeart)
                .count();

        boolean queenOfSpadesInHand = hand.stream().anyMatch(HeartsGameState::isQueenOfSpades);

        return heartsInHand < 3 || !queenOfSpadesInHand;
    }

    private Card playLeadCard(HeartsGameState gameState) {
        boolean heartsBroken = gameState.isHeartsBroken();
        List<Card> nonHeartsCards = hand.stream()
                .filter(HeartsGameState::isHeart)
                .toList();

        if (!heartsBroken && !nonHeartsCards.isEmpty()) {
            return playLowestCard(nonHeartsCards);
        } else {
            return playLowestCard(hand);
        }
    }

    private Card playLowestCard(List<Card> cards) {
        Card lowestCard = cards.get(0);
        for (Card card : cards) {
            if (card.getValue().getVal() < lowestCard.getValue().getVal()) {
                lowestCard = card;
            }
        }
        hand.remove(lowestCard);
        return lowestCard;
    }

    private Card playHighestCard(List<Card> cards) {
        Card highestCard = cards.get(0);
        for (Card card : cards) {
            if (card.getValue().getVal() > highestCard.getValue().getVal()) {
                highestCard = card;
            }
        }
        hand.remove(highestCard);
        return highestCard;
    }

    private Card playHighCard() {
        Card highestCard = hand.get(0);
        for (Card card : hand) {
            if (card.getValue().getVal() > highestCard.getValue().getVal()) {
                highestCard = card;
            }
        }
        hand.remove(highestCard);
        return highestCard;
    }

    @Override
    public List<Card> getPassedCards() {
        List<Card> cardsToPass = new ArrayList<>();

        // Standard strategy: pass high-value cards and hearts
        List<Card> highValueCards = hand.stream()
                .filter(card -> card.getValue().getVal() >= 10 || HeartsGameState.isQueenOfSpades(card))
                .sorted(Comparator.comparingInt(Card::getRank).reversed())
                .toList();

        List<Card> hearts = hand.stream()
                .filter(card -> card.getSuit() == Card.Suit.HEART)
                .sorted(Comparator.comparingInt(Card::getRank).reversed())
                .toList();

        cardsToPass.addAll(highValueCards.subList(0, Math.min(3, highValueCards.size())));
        cardsToPass.addAll(hearts.subList(0, Math.max(0, 3 - cardsToPass.size())));

        // Remove selected cards from hand
        hand.removeAll(cardsToPass);
        return cardsToPass;
    }
}
