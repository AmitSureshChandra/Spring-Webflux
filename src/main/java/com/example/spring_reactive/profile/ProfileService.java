package com.example.spring_reactive.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProfileService {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ProfileRepository profileRepository;

    @Autowired
    public ProfileService(ApplicationEventPublisher applicationEventPublisher, ProfileRepository profileRepository) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.profileRepository = profileRepository;
    }

    public Flux<Profile> all(){
        return profileRepository.findAll();
    }

    public Mono<Profile> get(String id){
        return profileRepository.findById(id);
    }

    public Mono<Profile> update(String id, String email){
        return profileRepository.findById(id)
                .map(profile -> new Profile(profile.getId(), email))
                .flatMap(this.profileRepository::save);
    }

    public Mono<Profile> delete(String id){
        return profileRepository
                .findById(id)
                .flatMap(profile -> profileRepository.deleteById(id).thenReturn(profile));
    }

    public Mono<Profile> create(String email){
        return profileRepository
                .save(new Profile(null, email))
                .doOnSuccess(profile -> this.applicationEventPublisher.publishEvent(new ProfileCreatedEvent(profile)));
    }
}
