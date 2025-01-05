package com.markndevon.cardgames.websocket;

import com.markndevon.cardgames.controller.HeartsController;
import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.message.GameUpdateMessage;
import com.markndevon.cardgames.message.LobbyUpdateMessage;
import com.markndevon.cardgames.model.gamestates.HeartsGameState;
import com.markndevon.cardgames.service.HeartsService;
import com.markndevon.cardgames.service.WebSocketHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class WebSocketSubscriptionListener {

    @Autowired
    @Lazy
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    @Lazy
    private final HeartsController heartsController;

    @Autowired
    private Logger logger;

    @Autowired
    private WebSocketHandlerService webSocketHandler;

    private final static Pattern GAME_ROOM_PATTERN = Pattern.compile("/topic/hearts/game-room/([0-9]+)", Pattern.CASE_INSENSITIVE);
    private final static Pattern LOBBY_PATTERN = Pattern.compile("/topic/hearts/game-lobby/([0-9]+)", Pattern.CASE_INSENSITIVE);
    private Message<byte[]> message;


    public WebSocketSubscriptionListener(SimpMessagingTemplate messagingTemplate, HeartsController heartsController) {
        this.messagingTemplate = messagingTemplate;
        this.heartsController = heartsController;
    }

    private static int matchesRoom(final String input, final Pattern pattern) {
        // Match regex against input
        final Matcher matcher = pattern.matcher(input);
        // Use results...
        int gameId = -1; // return -1 if expression does not match

        if (matcher.matches()) {
            String gameIdStr = matcher.group(1);
            gameId = Integer.parseInt(gameIdStr);
        }
        return gameId;
    }

    private static int matchesGameRoom(final String input) {
        return matchesRoom(input, GAME_ROOM_PATTERN);
    }

    private static int matchesLobby(final String input) {
        return matchesRoom(input, LOBBY_PATTERN);
    }

    @EventListener
    public void handleWebSocketSubscription(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        int extractedGameRoomId = matchesGameRoom(destination);
        int extractedLobbyId = matchesLobby(destination);

        StompHeaderAccessor messageAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = messageAccessor.getSessionAttributes();
        assert sessionAttributes != null : "SESSION ATTRIBUTES NOT POPULATED";
        String username = (String) sessionAttributes.get("username");
        assert username != null : "USERNAME NOT SPECIFIED IN SESSION ATTRIBUTES";

        if(extractedGameRoomId > 0){
            assert destination != null;
            webSocketHandler.cancelRemoval(username);

            logger.log("DETECTED CONNECTION TO GAME ROOM" + extractedLobbyId + ", BROADCASTING GAME STATE");
            HeartsGameState currGameState =
                    (HeartsGameState) heartsController.getGameService(extractedGameRoomId).getGameState();
            GameUpdateMessage currGameStateMessage = new GameUpdateMessage(currGameState);
            // TODO: Maybe only send to connecting user so we're not spamming everyone
            messagingTemplate.convertAndSend(destination, currGameStateMessage);
        }

        if(extractedLobbyId > 0){
            assert destination != null;
            webSocketHandler.cancelRemoval(username);

            logger.log("DETECTED CONNECTION TO LOBBY " + extractedLobbyId + ", BROADCASTING LOBBY STATE");
            HeartsService heartsService = (HeartsService) heartsController.getGameService(extractedLobbyId);
            LobbyUpdateMessage currLobbyMessage = new LobbyUpdateMessage(heartsService.getPlayers(), heartsService.getRulesConfig());
            messagingTemplate.convertAndSend(destination, currLobbyMessage);
        }
    }

    // TODO: Note to self, there should be some leniency before removing the user for both disconnects and unsubscribes
    //      right now, if you refresh the page on the lobby or game the websocket will disconnect then reconnect
    //      We need to give the user at least a few seconds (configurable value) to reconnect
    //      there could just be network troubles which could cause a disconnect, and we should allow that as well
    public void handleDisconnectOrUnsubscribe(AbstractSubProtocolEvent event) {
        logger.log("WEBSOCKET DISCONNECT DETECTED");

        StompHeaderAccessor messageAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = messageAccessor.getSessionAttributes();
        assert sessionAttributes != null : "SESSION ATTRIBUTES NOT POPULATED";
        String username = (String) sessionAttributes.get("username");

        assert username != null : "USERNAME NOT SPECIFIED IN SESSION ATTRIBUTES";
        logger.log("Username " + username);

        webSocketHandler.scheduleRemoval(username);
    }

    @EventListener
    public void handleWebSocketUnsubscribe(SessionUnsubscribeEvent event){
        handleDisconnectOrUnsubscribe(event);
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event){
        handleDisconnectOrUnsubscribe(event);
    }

}