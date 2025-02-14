package com.markndevon.cardgames.service;

import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.model.config.HeartsRulesConfig;
import com.markndevon.cardgames.model.config.RulesConfig;
import com.markndevon.cardgames.model.gamestates.GameType;
import com.markndevon.cardgames.model.player.HumanPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Factory class for creating a game service
 */
@Component
public class GameServiceFactory {

    @Autowired
    @Lazy
    SimpMessagingTemplate clientMessenger;

    @Autowired
    Logger logger;

    public GameServiceFactory() {
    }

    public GameService createGameService(GameType gameType, int gameId, RulesConfig rulesConfig, HumanPlayer gameOwner) {
        return switch (gameType) {
            case HEARTS -> new HeartsService(gameId, (HeartsRulesConfig) rulesConfig, gameOwner, clientMessenger, logger);
            // Add other games here
            default -> throw new IllegalArgumentException("Unsupported game type: " + gameType);
        };
    }

}
