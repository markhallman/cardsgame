package com.markndevon.cardgames.websocket.config;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
    public WebSocketEventListener(){
        // do nothing
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        // TODO: Need to gracefully handle websocket disconnects
    }
}
