package com.markndevon.cardgames.message;

import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.player.HumanPlayer;
import com.markndevon.cardgames.model.player.Player;

public class CreateGameMessage {

    private final RulesConfig rulesConfig;

    public CreateGameMessage() {
        this(null);
    }

    public CreateGameMessage(final RulesConfig rulesConfig) {
        this.rulesConfig = rulesConfig;
    }

    public RulesConfig getRulesConfig() {
        return rulesConfig;
    }

}
