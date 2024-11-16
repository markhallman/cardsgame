package com.markndevon.cardgames.model.config;

public class HeartsRulesConfig implements RulesConfig {

    // two of clubs leads
    public static final int START_CARD_RULES_2_CLUBS = 0;
    // players cannot play a point during the first trick
    public static final int START_CARD_RULES_NO_POINTS = 1;
    // players can play whatever they want
    public static final int START_CARD_RULES_ANARCHY = 2;

    //region RuleDefaults
    private static final boolean KITTY_WON_FIRST_TRICK_DEFAULT = false;
    private static final boolean JACK_REQUIRED_DEFAULT = false;

    private static final boolean POINTS_ALLOWED_FIRST_TRICK_DEFAULT = false;
    private static final boolean HEARTS_MUST_BE_BROKEN_DEFAULT = true;
    private static final boolean JACK_MINUS_10_DEFAULT = true;
    private static final boolean SHOOT_THE_SUN_DEFAULT = false;
    private static final boolean NO_TRICKS_MINUS_5_DEFAULT = false;
    private static final int POINTS_TO_LOSE_DEFAULT = 100;
    private static final int FIRST_CARD_RULE_DEFAULT = START_CARD_RULES_2_CLUBS;

    //endregion

    public enum BooleanRule {
        KITTY_WON_FIRST_TRICK("Player who wins the first trick wins the kitty", KITTY_WON_FIRST_TRICK_DEFAULT),
        JACK_REQUIRED("Is the Jack of Diamonds needed to shoot the moon?", JACK_REQUIRED_DEFAULT),
        POINTS_ALLOWED_FIRST_TRICK("Points are allowed to be played on the first trick", POINTS_ALLOWED_FIRST_TRICK_DEFAULT),
        HEARTS_MUST_BE_BROKEN("Hearts may not be played until they are broken", HEARTS_MUST_BE_BROKEN_DEFAULT),
        JACK_MINUS_10("Jack of Diamonds worth -10", JACK_MINUS_10_DEFAULT),
        SHOOT_THE_SUN("Shoot the Sun", SHOOT_THE_SUN_DEFAULT),
        NO_TRICKS_MINUS_5("Taking no tricks is worth -5", NO_TRICKS_MINUS_5_DEFAULT);

        private final String description;
        private final boolean defaultValue;
        private BooleanRule(final String description, final boolean defaultValue) {
            this.description = description;
            this.defaultValue = defaultValue;
        }

        public String getDescription() {
            return description;
        }

        public boolean getDefaultValue() {
            return defaultValue;
        }
    }

    public enum IntegerRules {
        POINTS_TO_LOSE("Game ends when the first player reaches this many points", POINTS_TO_LOSE_DEFAULT),
        FIRST_TRICK("What is allowed to be lead on the first trick?", FIRST_CARD_RULE_DEFAULT);

        private final String description;
        private final int defaultValue;
        private IntegerRules(final String description, final int defaultValue) {
            this.description = description;
            this.defaultValue = defaultValue;
        }

        public String getDescription() {
            return description;
        }

        public int getDefaultValue() {
            return defaultValue;
        }
    }

    //region RulesVariables

    // 0 - the first player leads the two of clubs
    // 1,2 - first player rotates each round
    // 1 - first player must not lead point cards
    // 2 - first player can lead anything
    private final int startCardRules;

    // whether players may play Hearts or the Queen of Spades on the first trick
    private final boolean pointsAllowedFirstTrick;

    // whether hearts must be "broken" to be lead
    // if false, players may lead hearts on the 3rd trick
    private final boolean heartsMustBeBroken;

    // whether the jack of diamonds is worth -10
    private final boolean jackMinus10;

    // whether taking no tricks is worth -5
    private final boolean noTricksMinus5;

    // whether taking all tricks is worth 52 to all other players
    private final boolean shootTheSun;

    // what the game goes to, e.g. 100 points
    private final int pointsToLose;

    // whether the player needs to win the Jack of Diamonds to shoot the moon
    private final boolean jackRequired;

    private final boolean kittyWonFirstTrick;

    //endregion

    private HeartsRulesConfig(final int startCardRules, final boolean pointsAllowedFirstTrick, final boolean heartsMustBeBroken, final boolean jackMinus10,
                        final boolean noTricksMinus5, final boolean shootTheSun, final int pointsToLose, final boolean jackRequired, final boolean kittyWonFirstTrick) {
        this.startCardRules = startCardRules;
        this.pointsAllowedFirstTrick = pointsAllowedFirstTrick;
        this.heartsMustBeBroken = heartsMustBeBroken;
        this.jackMinus10 = jackMinus10;
        this.noTricksMinus5 = noTricksMinus5;
        this.shootTheSun = shootTheSun;
        this.pointsToLose = pointsToLose;
        this.jackRequired = jackRequired;
        this.kittyWonFirstTrick = kittyWonFirstTrick;

        if(startCardRules != START_CARD_RULES_2_CLUBS && startCardRules != START_CARD_RULES_NO_POINTS
                && startCardRules != START_CARD_RULES_ANARCHY) {
            throw new IllegalArgumentException("startCardRules must be 0 (start with 2C), 1 (no points), or 2 (anarchy)");
        }
        if(jackRequired && !jackMinus10) {
            throw new IllegalArgumentException("Jack of Diamonds can not be required if it is not worth -10");
        }
        if(startCardRules == START_CARD_RULES_ANARCHY && pointsAllowedFirstTrick) {
            throw new IllegalArgumentException("Anarchy start rules can not be enabled if no points are allowed on the first trick");
        }
    }

    public int getStartCardRules() {
        return startCardRules;
    }

    public boolean isPointsAllowedFirstTrick() {
        return pointsAllowedFirstTrick;
    }

    public boolean isHeartsMustBeBroken() {
        return heartsMustBeBroken;
    }

    public boolean isJackMinus10() {
        return jackMinus10;
    }

    public boolean isNoTricksMinus5() {
        return noTricksMinus5;
    }

    public boolean isShootTheSun() {
        return shootTheSun;
    }

    public int getPointsToLose() {
        return pointsToLose;
    }

    public boolean isJackRequired() {
        return jackRequired;
    }

    public boolean isKittyWonFirstTrick() {
        return kittyWonFirstTrick;
    }

    public boolean getRule(final BooleanRule booleanRule) {
        switch (booleanRule) {
            case KITTY_WON_FIRST_TRICK -> { return isKittyWonFirstTrick(); }
            case JACK_REQUIRED -> { return isJackRequired(); }
            case POINTS_ALLOWED_FIRST_TRICK -> { return isPointsAllowedFirstTrick(); }
            case HEARTS_MUST_BE_BROKEN -> { return isHeartsMustBeBroken(); }
            case JACK_MINUS_10 -> { return isJackMinus10(); }
            case SHOOT_THE_SUN -> { return isShootTheSun(); }
            case NO_TRICKS_MINUS_5 -> { return isNoTricksMinus5(); }
            default -> { throw new IllegalStateException(); }
        }
    }
    public int getRule(final IntegerRules integerRules) {
        switch (integerRules) {
            case POINTS_TO_LOSE -> { return getPointsToLose(); }
            case FIRST_TRICK -> { return getStartCardRules(); }
            default -> throw new IllegalStateException();
        }
    }

    public static class Builder {

        private int startCardRules = START_CARD_RULES_2_CLUBS;
        private boolean pointsFirstTrick = POINTS_ALLOWED_FIRST_TRICK_DEFAULT;
        private boolean heartsMustBeBroken = HEARTS_MUST_BE_BROKEN_DEFAULT;
        private boolean jackMinus10 = JACK_MINUS_10_DEFAULT;

        private boolean noTricksMinus5 = NO_TRICKS_MINUS_5_DEFAULT;
        private boolean shootTheSun = SHOOT_THE_SUN_DEFAULT;

        private int pointsToLose = POINTS_TO_LOSE_DEFAULT;
        private boolean jackRequired = JACK_REQUIRED_DEFAULT;
        private boolean kittyWonFirstTrick = KITTY_WON_FIRST_TRICK_DEFAULT;

        public Builder() {}

        public Builder setStartCardRules(final int startCardRules) {
            this.startCardRules = startCardRules;
            return this;
        }

        public Builder setPointsFirstTrick(final boolean pointsFirstTrick) {
            this.pointsFirstTrick = pointsFirstTrick;
            return this;
        }

        public Builder setHeartsMustBeBroken(final boolean heartsMustBeBroken) {
            this.heartsMustBeBroken = heartsMustBeBroken;
            return this;
        }

        public Builder setJackMinus10(final boolean jackMinus10) {
            this.jackMinus10 = jackMinus10;
            return this;
        }

        public Builder setNoTricksMinus5(final boolean noTricksMinus5) {
            this.noTricksMinus5 = noTricksMinus5;
            return this;
        }

        public Builder setShootTheSun(final boolean shootTheSun) {
            this.shootTheSun = shootTheSun;
            return this;
        }

        public void setPointsToLose(int pointsToLose) {
            this.pointsToLose = pointsToLose;
        }

        public Builder setJackRequired(final boolean jackRequired) {
            this.jackRequired = jackRequired;
            return this;
        }

        public void setKittyWonFirstTrick(boolean kittyWonFirstTrick) {
            this.kittyWonFirstTrick = kittyWonFirstTrick;
        }
        public void setRule(final BooleanRule booleanRule, final boolean value) {
            switch (booleanRule) {
                case KITTY_WON_FIRST_TRICK -> setKittyWonFirstTrick(value);
                case JACK_REQUIRED -> setJackRequired(value);
                case POINTS_ALLOWED_FIRST_TRICK -> setPointsFirstTrick(value);
                case HEARTS_MUST_BE_BROKEN -> setHeartsMustBeBroken(value);
                case JACK_MINUS_10 -> setJackMinus10(value);
                case SHOOT_THE_SUN -> setShootTheSun(value);
                case NO_TRICKS_MINUS_5 -> setNoTricksMinus5(value);
            }
        }
        public void setRule(final IntegerRules integerRules, final int value) {
            switch (integerRules) {
                case POINTS_TO_LOSE -> setPointsToLose(value);
                case FIRST_TRICK -> setStartCardRules(value);
            }
        }
        public RulesConfig build() {
            return new HeartsRulesConfig(startCardRules, pointsFirstTrick, heartsMustBeBroken,
                    jackMinus10, noTricksMinus5, shootTheSun, pointsToLose, jackRequired, kittyWonFirstTrick);
        }
    }
}
