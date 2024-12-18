package com.markndevon.cardgames.websocket;

import com.markndevon.cardgames.controller.HeartsController;
import com.markndevon.cardgames.message.GameUpdateMessage;
import com.markndevon.cardgames.model.gamestates.HeartsGameState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class WebSocketSubscriptionListener implements ApplicationListener<SessionSubscribeEvent> {

    @Autowired
    @Lazy
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    @Lazy
    private final HeartsController heartsController;

    private final static Pattern GAME_ROOM_PATTERN = Pattern.compile("/topic/hearts/game-room/([0-9]+)", Pattern.CASE_INSENSITIVE);


    public WebSocketSubscriptionListener(SimpMessagingTemplate messagingTemplate, HeartsController heartsController) {
        this.messagingTemplate = messagingTemplate;
        this.heartsController = heartsController;
    }

    private static int matchesGameRoom(final String input) {
        // Match regex against input
        final Matcher matcher = GAME_ROOM_PATTERN.matcher(input);
        // Use results...
        int gameId = -1; // return -1 if expression does not match

        if (matcher.matches()) {
            String gameIdStr = matcher.group(1);
            gameId = Integer.parseInt(gameIdStr);
        }
        return gameId;
    }

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        int extractedGameId = matchesGameRoom(destination);

        System.out.println("Extracted gameID " + extractedGameId);
        if(extractedGameId > 0){
            System.out.println("DETECTED CONNECTION TO GAMEROOM, BROADCASTING GAMESTATE");
            HeartsGameState currGameState =
                    (HeartsGameState) heartsController.getGameService(extractedGameId).getGameState();
            GameUpdateMessage currGameStateMessage = new GameUpdateMessage(currGameState);
            messagingTemplate.convertAndSend(destination, currGameStateMessage);
        }
    }
}