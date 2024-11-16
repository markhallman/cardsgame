package com.markndevon.cardgames.message.json;

public interface JSONConstants {
    String PLAYER = "player";
    String PASSER = "passer";
    String RECEIVER = "receiver";
    String CARD = "card";
    String MESSAGE = "message";

    String IS_LOBBY_HEAD = "isLobbyHead";

    String RULES_CONFIG_KEY = "rulesConfig";
    String PLAYER_ARRAY_KEY = "players";
    String CARD_ARRAY_KEY = "cards";
    String MESSAGE_TYPE_KEY = "messageType";
    String PLAYER_NAME = "playerName";
    String PLAYER_ID = "playerID";

    String IS_HUMAN_CONTROLLED = "isHumanControlled";
    String MAP_KEY = "map";
    String GAME_DONE_KEY = "gameDone";
    String HAND_DONE_KEY = "handDone";

    // region rulesConfig
    String RULES_CONFIG_START_RULE = "startRule";
    String RULES_CONFIG_POINTS_TO_LOSE = "pointsToLose";
    String RULES_CONFIG_KITTY_RULE = "kittyRule";
    String RULES_CONFIG_JACK_REQUIRED_RULE = "jackRequiredRule";
    String RULES_CONFIG_FIRST_TRICK_RULE = "firstTrickRule";
    String RULES_CONFIG_HEARTS_BROKEN_RULE = "heartBrokensRule";
    String RULES_CONFIG_JACK_MINUS_10_RULE = "jackMinus10Rule";
    String RULES_CONFIG_SHOOT_THE_SUN_RULE = "shootTheSunRule";
    String RULES_CONFIG_NO_TRICKS_RULE = "noTricksRule";
    //endregion
}
