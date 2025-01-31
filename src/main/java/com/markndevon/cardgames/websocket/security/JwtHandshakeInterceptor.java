package com.markndevon.cardgames.websocket.security;

import com.markndevon.cardgames.controller.UserController;
import com.markndevon.cardgames.service.authentication.CardsUserDetailsService;
import com.markndevon.cardgames.service.authentication.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    @Lazy
    private UserController userController;

    public static final String USERNAME_ATTRIBUTE = "username";

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();

            // Now you can pass it to your auth check
            ResponseEntity<?> authResponse = userController.checkAuth(httpServletRequest);
            if (authResponse.getStatusCode() == HttpStatus.OK){
                UserDetails userDetails = (UserDetails) authResponse.getBody();
                assert userDetails != null;

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