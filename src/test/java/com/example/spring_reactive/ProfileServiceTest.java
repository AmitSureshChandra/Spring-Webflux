package com.example.spring_reactive;
import com.example.spring_reactive.profile.Profile;
import com.example.spring_reactive.profile.ProfileRepository;
import com.example.spring_reactive.profile.ProfileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.function.Predicate;

@DataMongoTest
@Import(ProfileService.class)
@DisplayName("Profile Service Test")
public class ProfileServiceTest {
    private final ProfileService profileService;
    private final ProfileRepository profileRepository;

    public ProfileServiceTest(@Autowired ProfileService profileService,@Autowired ProfileRepository profileRepository) {
        this.profileService = profileService;
        this.profileRepository = profileRepository;
    }

    @Test
    @DisplayName("Test for getting all profiles")
    public void getAll(){
        Flux<Profile> saved = profileRepository.saveAll(
                Flux.just(
                        new Profile(null, "Amit"),
                        new Profile(null, "Rahul"),
                        new Profile(null, "Saurabh")
                )
        );

        Flux<Profile> composite = profileService.all().thenMany(saved);
        Predicate<Profile> match = profile -> saved.any(savedItem -> savedItem.equals(profile)).block();

        StepVerifier
                .create(composite)
                .expectNextMatches(match)
                .expectNextMatches(match)
                .expectNextMatches(match)
                .verifyComplete();
    }
}
