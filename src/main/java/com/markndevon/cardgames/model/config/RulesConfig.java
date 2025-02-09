package com.markndevon.cardgames.model.config;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "gameType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = HeartsRulesConfig.class, name = "HEARTS")
        // Add other game rule configurations here
})
public abstract class RulesConfig {
    protected int numPlayers;
    public RulesConfig(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public int getNumPlayers() {
        return numPlayers;
    }
}
