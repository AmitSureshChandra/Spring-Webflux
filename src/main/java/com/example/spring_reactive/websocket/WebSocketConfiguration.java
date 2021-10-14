package com.example.spring_reactive.websocket;
import com.example.spring_reactive.profile.ProfileCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class WebSocketConfiguration {
    @Bean
    Executor executor(){
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    HandlerMapping handlerMapping(WebSocketHandler handler){
        return new SimpleUrlHandlerMapping(){
            {
                setUrlMap(Collections.singletonMap("/ws/profiles",handler));
            }
        };
    }

    @Bean
    WebSocketHandlerAdapter webSocketHandlerAdapter(){
        return new WebSocketHandlerAdapter();
    }

    @Bean
    WebSocketHandler webSocketHandler(
            ObjectMapper objectMapper,
            ProfileCreatedEventPublisher eventPublisher
    ) {

        Flux<ProfileCreatedEvent> publish = Flux.create(eventPublisher).share();

        return session -> {
            Flux<WebSocketMessage> messageFlux = publish
                    .map(evt -> {
                        try {
                            return objectMapper.writeValueAsString(evt.getSource());
                        }
                        catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .map(str -> {
                        System.out.println("sending " + str);
                        return session.textMessage(str);
                    });

            return session.send(messageFlux);
        };
    }
}
