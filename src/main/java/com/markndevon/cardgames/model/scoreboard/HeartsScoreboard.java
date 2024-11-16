package com.markndevon.cardgames.model.scoreboard;

import com.markndevon.cardgames.model.Card;
import com.markndevon.cardgames.model.config.HeartsRulesConfig;

import com.markndevon.cardgames.model.gamestates.HeartsGameState;
import com.markndevon.cardgames.model.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeartsScoreboard extends ScoreBoard{

    private Player.PlayerDescriptor playerWhoWonJack = null;
    private Player.PlayerDescriptor playerWhoShotMoon = null;

    public HeartsScoreboard(HeartsGameState game) {
        super(game);
    }

    public HeartsScoreboard(Player.PlayerDescriptor[] players, HeartsRulesConfig rulesConfig) {
        super(players, rulesConfig);
    }

    public void updateScore(final Map<Player.PlayerDescriptor, List<Card>> cardsWonByPlayer) {
        final Map<Player.PlayerDescriptor, Integer> handScore = new HashMap<>();

        // calculate preliminary score per player, before shoot the moon logic
        // includes bonus for taking no tricks, as we want shooting the moon to override
        for(final Player.PlayerDescriptor player: players) {
            final int prelimScore = scorePlayer(player, cardsWonByPlayer);
            handScore.put(player, prelimScore);
        }

        adjustIfMoonShot(handScore);
        adjustIfSunShot(handScore, cardsWonByPlayer);

        // null check for testing. Don't expect a null in a real game
        if(((HeartsRulesConfig) rulesConfig).isJackMinus10() && playerWhoWonJack != null) {
            handScore.put(playerWhoWonJack, handScore.get(playerWhoWonJack) - 10);
        }

        updateCurrentScore(handScore);
        reset();
    }

    private void reset() {
        playerWhoWonJack = null;
        playerWhoShotMoon = null;
    }

    private int scorePlayer(final Player.PlayerDescriptor player, final Map<Player.PlayerDescriptor, List<Card>> cardsWonByPlayer) {
        final List<Card> cardsWon = cardsWonByPlayer.get(player);

        if(((HeartsRulesConfig)rulesConfig).isNoTricksMinus5() && cardsWon.isEmpty()) {
            return -5;
        }

        int score = 0;
        for(final Card card : cardsWon) {
            final int cardScore = scoreCard(card, player);
            score += cardScore;
        }

        if(score == 26) {
            playerWhoShotMoon = player;
        }

        return score;
    }

    private int scoreCard(final Card card, final Player.PlayerDescriptor player) {
        if(HeartsGameState.isHeart(card)) {
            return 1;
        } else if(HeartsGameState.isJackOfDiamonds(card)) {
            playerWhoWonJack = player;
            return 0;
        } else if(HeartsGameState.isQueenOfSpades(card)) {
            return 13;
        }

        return 0;
    }

    private void adjustIfMoonShot(final Map<Player.PlayerDescriptor, Integer> handScore) {
        if(playerWhoShotMoon != null) {
            if(((HeartsRulesConfig) rulesConfig).isJackRequired() && !playerWhoShotMoon.equals(playerWhoWonJack)) {
                return;
            }

            for(final Player.PlayerDescriptor player : players) {
                if(player.equals(playerWhoShotMoon)) {
                    handScore.put(player, 0);
                } else {
                    handScore.put(player, 26);
                }
            }
        }
    }
    private void adjustIfSunShot(final Map<Player.PlayerDescriptor, Integer> handScore,
                                 final Map<Player.PlayerDescriptor, List<Card>> cardsWonByPlayer) {
        if(((HeartsRulesConfig) rulesConfig).isShootTheSun()) {
            int numEmpty = 0;
            for (final Player.PlayerDescriptor player : players) {
                if (cardsWonByPlayer.get(player).isEmpty()) {
                    numEmpty++;
                }
            }

            if (numEmpty == players.length - 1) {
                for (final Player.PlayerDescriptor player : players) {
                    if (cardsWonByPlayer.get(player).isEmpty()) {
                        handScore.put(player, 52);
                    } else {
                        handScore.put(player, 0);
                    }
                }
            }
        }
    }
}
