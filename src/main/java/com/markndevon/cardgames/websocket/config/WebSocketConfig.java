package com.markndevon.cardgames.websocket.config;

import com.markndevon.cardgames.controller.HeartsController;
import com.markndevon.cardgames.message.GameUpdateMessage;
import com.markndevon.cardgames.model.gamestates.HeartsGameState;
import com.markndevon.cardgames.service.authentication.CardsUserDetailsService;
import com.markndevon.cardgames.service.authentication.JWTService;
import com.markndevon.cardgames.websocket.WebSocketUserIdentifier;
import com.markndevon.cardgames.websocket.security.JwtHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
@EnableWebSocketMessageBroker
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
                .addInterceptors(new JwtHandshakeInterceptor(jwtService, userDetailsService));
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setUserDestinationPrefix("/user"); // Configure user-specific messaging
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                var authentication = SecurityContextHolder.getContext().getAuthentication();
                // TODO: RIGHT NOW NO USERNAME IN HEADER SO THIS IS USELESS
                String username = (authentication != null) ? authentication.getName() : "anonymousUser";

                // Check if the message is a SUBSCRIBE frame
                if (Objects.requireNonNull(message.getHeaders().get("simpMessageType")).toString().equals("SUBSCRIBE")) {
                    String destination = (String) message.getHeaders().get("simpDestination");
                    System.out.println("User '" + username + "' subscribed to: " + destination);
                }

                // TODO: Is this necessary?
                // Add the username to the message headers
                Message newMessage = MessageBuilder.fromMessage(message)
                                        .setHeader("username", username)
                                        .build();

                return newMessage;
            }
        });
    }
}
