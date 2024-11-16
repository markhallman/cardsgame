package com.markndevon.cardgames.message;

import com.markndevon.cardgames.model.config.RulesConfig;


public class StartGameRequest extends Message {

    private final RulesConfig rulesConfig;

    public StartGameRequest(final RulesConfig rulesConfig) {
        this.rulesConfig = rulesConfig;
    }

    public RulesConfig getRulesConfig() {
        return rulesConfig;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.StartGameRequest;
    }
}
