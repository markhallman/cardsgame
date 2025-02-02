package com.markndevon.cardgames.websocket.config;

import com.markndevon.cardgames.controller.HeartsController;
import com.markndevon.cardgames.service.authentication.CardsUserDetailsService;
import com.markndevon.cardgames.service.authentication.JWTService;
import com.markndevon.cardgames.websocket.security.JwtHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@SuppressWarnings("unused")
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    @Lazy
    private HeartsController heartsController;

    @Autowired
    @Lazy
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private CardsUserDetailsService userDetailsService;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new JwtHandshakeInterceptor(jwtService));
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setUserDestinationPrefix("/user"); // Configure user-specific messaging
    }
}
