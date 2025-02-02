package com.markndevon.cardgames.websocket.security;

import com.markndevon.cardgames.service.authentication.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private final JWTService jwtService;

    public static final String USERNAME_ATTRIBUTE = "username";

    public JwtHandshakeInterceptor(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            @NonNull Map<String, Object> attributes) throws IllegalStateException{
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
            UserDetails userDetails = jwtService.getUserDetailsFromRequestAndValidate(httpServletRequest);

            if(userDetails != null){
                attributes.put(USERNAME_ATTRIBUTE, userDetails.getUsername());
                return true;
            }

        } else {
            throw new IllegalStateException("WebSocket handshake request is not an HTTP request");
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }

}