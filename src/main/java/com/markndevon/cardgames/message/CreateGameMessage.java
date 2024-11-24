package com.markndevon.cardgames.message;

import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.player.HumanPlayer;
import com.markndevon.cardgames.model.player.Player;

public class CreateGameMessage {

    private final RulesConfig rulesConfig;
    private final Player.PlayerDescriptor creatingPlayer;

    public CreateGameMessage(final Player.PlayerDescriptor creatingPlayer) {
        this(creatingPlayer, null);
    }

    public CreateGameMessage(final Player.PlayerDescriptor creatingPlayer, final RulesConfig rulesConfig) {
        this.rulesConfig = rulesConfig;
        this.creatingPlayer = creatingPlayer;
    }

    public RulesConfig getRulesConfig() {
        return rulesConfig;
    }

    public Player.PlayerDescriptor getCreatingPlayer() {
        return creatingPlayer;
    }

}
