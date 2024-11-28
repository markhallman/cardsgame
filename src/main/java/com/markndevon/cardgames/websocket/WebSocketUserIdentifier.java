package com.markndevon.cardgames.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

//TODO: Is this necessary anymore?
@Component
public class WebSocketUserIdentifier implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        // Assume a "playerName" parameter is passed during the handshake
        // TODO: add this info to the player somehow so we can identify the player from the actual player object
        String userToken = request.getHeaders().getFirst("Authorization");
        if (userToken != null) {
            String username = extractUsernameFromToken(userToken); // Implement this logic
            attributes.put("user", username); // Store username in session attributes
        }
        return true;
    }

    private String extractUsernameFromToken(String userToken) {
        // TODO: parse this based on however I decide client to send along username
        return userToken;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // Do nothing
    }
}
