package com.example.spring_reactive.profile;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ProfileEndpointConfiguration {
    @Bean
    RouterFunction<ServerResponse> routes(ProfileHandler profileHandler){
            return route(GET("/profiles"), profileHandler::all)
                    .andRoute(GET("/profiles/{id}"), profileHandler::getById)
                    .andRoute(POST("/profiles"), profileHandler::create)
                    .andRoute(DELETE("/profiles/{id}"), profileHandler::deleteById)
                    .andRoute(PUT("/profiles/{id}"), profileHandler::updateById);
    }
}
