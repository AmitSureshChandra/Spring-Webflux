package com.example.spring_reactive;

import com.example.spring_reactive.profile.Profile;
import com.example.spring_reactive.profile.ProfileRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Component
@org.springframework.context.annotation.Profile("demo")
public class SimpleDataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private ProfileRepository profileRepository;

    public SimpleDataInitializer(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        profileRepository
                .deleteAll()
                .thenMany(
                        Flux
                                .just("A","B","C","D")
                                .map(name -> new Profile(UUID.randomUUID().toString(), name+ "@gmail.com"))
                                .flatMap(profileRepository::save)
                )
                .thenMany(profileRepository.findAll())
                .subscribe(profile -> System.out.println("saving " + profile.toString()));
    }
}
