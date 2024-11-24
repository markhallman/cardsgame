package com.markndevon.cardgames.message;

import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.player.HumanPlayer;
import com.markndevon.cardgames.model.player.Player;

public class CreateGameMessage {

    private final RulesConfig rulesConfig;
    private final HumanPlayer creatingPlayer;

    public CreateGameMessage(final HumanPlayer creatingPlayer) {
        this(creatingPlayer, RulesConfig.getDefault());
    }

    public CreateGameMessage(final HumanPlayer creatingPlayer, final RulesConfig rulesConfig) {
        this.rulesConfig = rulesConfig;
        this.creatingPlayer = creatingPlayer;
    }

    public RulesConfig getRulesConfig() {
        return rulesConfig;
    }

    public HumanPlayer getCreatingPlayer() {
        return creatingPlayer;
    }

}
