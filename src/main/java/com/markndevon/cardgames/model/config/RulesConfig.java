package com.markndevon.cardgames.model.config;

public interface RulesConfig {
    static RulesConfig getDefault(){
        // Subclasses should implement, but you cant return a default without knowing what game you're playing
        throw new UnsupportedOperationException();
    }
}
