package com.markndevon.cardgames.model.util.hearts;

import com.markndevon.cardgames.model.config.HeartsRulesConfig;


// todo make the number of cards and the directions configurable?
public class PassDeterminer {

    private final int numPlayers;
    private final int numCards;

    public PassDeterminer(final int numPlayers, final HeartsRulesConfig rulesConfig) {
        this.numPlayers = numPlayers;
        this.numCards = 3;
    }

    /*
    Pass rotations extend the scheme used in smaller games.
    Begin by passing to the player immediately to the left, then the right.
    Follow by passing two spaces to the left and right, then three spaces, then four,
    and so on, until each player has passed to each other player once.
    The final hand in the rotation is the "hold hand" in which no one passes.
     */
    public PassingRule getPassingRule(final int handCount) {
        final int passSituation = (handCount + 1) % numPlayers;

        if(passSituation == 0) { // hold hand
            return new PassingRule(PassingRule.PassDirection.NONE, 0, 0);
        } else if(passSituation % 2 == 1) { // odd turn
            return new PassingRule(PassingRule.PassDirection.LEFT, (passSituation / 2) + 1, numCards);
        } else { //even turn
            return new PassingRule(PassingRule.PassDirection.RIGHT, passSituation / 2, numCards);
        }
    }

    public static class PassingRule {

        public enum PassDirection {
            LEFT, RIGHT, NONE
        }
        private final PassDirection passDirection;
        private final int xPlayers;
        private final int numCards;
        public PassingRule(final PassDirection passDirection, final int xPlayers, final int numCards) {
            this.passDirection = passDirection;
            this.xPlayers = xPlayers;
            this.numCards = numCards;
        }

        public PassDirection getPassDirection() {
            return passDirection;
        }

        public int getXPlayers() {
            return xPlayers;
        }

        public int getNumCards() {
            return numCards;
        }
    }
}
