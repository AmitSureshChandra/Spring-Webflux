package com.example.spring_reactive.profile;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ProfileHandler {

    private final ProfileService profileService;

    public ProfileHandler(@Autowired ProfileService profileService) {
        this.profileService = profileService;
    }

    Mono<ServerResponse> getById(ServerRequest r){
        return  defaultReadResponse(this.profileService.get(id(r)));
    }

    Mono<ServerResponse> all(ServerRequest r){
        return  defaultReadResponse(this.profileService.all());
    }

    Mono<ServerResponse> deleteById(ServerRequest r){
        return  defaultReadResponse(this.profileService.delete(id(r)));
    }

    Mono<ServerResponse> updateById(ServerRequest r){
        return defaultReadResponse(
            r.bodyToFlux(Profile.class)
                    .flatMap(profile -> this.profileService.update(id(r),profile.getEmail()))
        );
    }

    Mono<ServerResponse> create(ServerRequest r){
        return defaultReadResponse(
                r.bodyToFlux(Profile.class)
                        .flatMap(toWrite -> this.profileService.create(toWrite.getEmail()))
        );
    }

    public static Mono<ServerResponse> defaultReadResponse(Publisher<Profile> profiles){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(profiles, Profile.class);
    }

    private static String id(ServerRequest r){
        return r.pathVariable("id");
    }
}
