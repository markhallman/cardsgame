package com.markndevon.cardgames.websocket.security;

import com.markndevon.cardgames.service.authentication.CardsUserDetailsService;
import com.markndevon.cardgames.service.authentication.JWTService;
import org.springframework.http.HttpStatus;
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

        String query = request.getURI().getQuery();
        System.out.println("Query: " + query);
        if (query != null && query.contains("token=")) {
            String token = query.split("token=")[1];
            String username = jwtService.extractUsername(token);

            System.out.println("Token: " + token + " Username: " + username);

            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                attributes.put("username", username);
                return jwtService.validateToken(token, userDetails);
            }
        }


        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }

}