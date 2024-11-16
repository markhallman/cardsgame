package com.markndevon.cardgames.model.scoreboard;

import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.gamestates.GameState;
import com.markndevon.cardgames.model.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Scoreboard class, for use from server AND client
 */
public abstract class ScoreBoard {

    protected final Player.PlayerDescriptor[] players;
    protected final RulesConfig rulesConfig;
    protected final Map<Player.PlayerDescriptor, Integer> currentScore = new HashMap<>();
    protected final List<Map<Player.PlayerDescriptor, Integer>> scoreHistory = new ArrayList<>();

    public ScoreBoard(final GameState game) {
        this(game.getPlayerDescriptors(), game.getRulesConfig());
    }

    public ScoreBoard(final Player.PlayerDescriptor[] players, final RulesConfig rulesConfig) {
        this.players = players;
        this.rulesConfig = rulesConfig;

        // Theoretically in the future, could be set with handicap scores passed from server
        final Map<Player.PlayerDescriptor, Integer> startingScores = new HashMap<>();
        for(Player.PlayerDescriptor currentPlayer : players){
            startingScores.put(currentPlayer, 0);
        }
        scoreHistory.add(startingScores);

        init();
    }

    public void init() {
        for(final Player.PlayerDescriptor player : players) {
            currentScore.put(player, 0);
        }
    }

    public Map<Player.PlayerDescriptor, Integer> getScore() {
        return currentScore;
    }

    public List<Map<Player.PlayerDescriptor, Integer>> getScoreHistory() { return scoreHistory; }

    // For use from the client, whatever we get from the server should just be treated as the current score
    public void overwriteCurrentScore(final Map<Player.PlayerDescriptor, Integer> handScoreMap){
        currentScore.putAll(handScoreMap);
    }
    public void updateCurrentScore(final Map<Player.PlayerDescriptor, Integer> handScoreMap){
        for(final Map.Entry<Player.PlayerDescriptor, Integer> handScoreEntry : handScoreMap.entrySet()) {
            final Player.PlayerDescriptor player = handScoreEntry.getKey();
            int prevPoints = currentScore.getOrDefault(player, 0);
            currentScore.put(player, prevPoints + handScoreEntry.getValue());
        }
    }

    public void saveHandScore() {
        scoreHistory.add(new HashMap<>(currentScore));
    }

}

