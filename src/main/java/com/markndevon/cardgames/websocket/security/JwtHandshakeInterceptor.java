package com.markndevon.cardgames.websocket.security;

import com.markndevon.cardgames.service.authentication.CardsUserDetailsService;
import com.markndevon.cardgames.service.authentication.JWTService;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private JWTService jwtService;
    private CardsUserDetailsService userDetailsService;

    public JwtHandshakeInterceptor(JWTService jwtService, CardsUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        String token = request.getHeaders().getFirst("Authorization").replace("Bearer ", "");
        String username = jwtService.extractUsername(token);

        if (username != null ) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            attributes.put("username", username);
            return jwtService.validateToken(token, userDetails);
        }

        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }

}