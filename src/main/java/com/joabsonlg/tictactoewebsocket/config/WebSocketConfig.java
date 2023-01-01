package com.joabsonlg.tictactoewebsocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration class for setting up WebSocket messaging in the application.
 * <p>
 * Enables the use of STOMP (Simple Text Oriented Messaging Protocol) for sending messages between clients and servers.
 *
 * @author Joabson Arley do Nascimento
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Registers the "/ws" endpoint, allowing clients to connect to the WebSocket message broker.
     *
     * @param registry the registry for registering STOMP endpoints
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    /**
     * Configures the message broker to use destination prefixes to filter messages.
     * All messages that start with "/app" are routed to the message-handling methods,
     * while messages that start with "/queue", "/topic", or "/user" are routed to the message broker.
     * The message broker broadcasts messages to subscribed clients that are connected to the broker.
     *
     * @param registry the registry for configuring the message broker
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/queue", "/topic", "/user");
        registry.setUserDestinationPrefix("/user");
    }
}
