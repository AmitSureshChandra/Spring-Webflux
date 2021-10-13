package com.example.spring_reactive.profile;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.net.URI;

@RestController
@RequestMapping(value = "/profiles", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@org.springframework.context.annotation.Profile("classic")
public class ProfileRestController {
    private final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;

    @Autowired
    private ProfileService profileService;

    @GetMapping
    public Publisher<Profile> getAll(){
        return profileService.all();
    }

    @GetMapping("/{id}")
    public Publisher<Profile> getById(@PathVariable("id") String id){
        return profileService.get(id);
    }

    @PostMapping
    public Publisher<ResponseEntity<Profile>> create(@RequestBody Profile profile){
        return profileService.create(profile.getEmail())
                .map(p -> ResponseEntity.created(URI.create("/profiles/" + p.getId()))
                .contentType(mediaType)
                .build());
    }

    @DeleteMapping("/{id}")
    public Publisher<Profile> deleteById(@PathVariable("id") String id){
        return profileService.delete(id);
    }

    @PutMapping("/{id}")
    public Publisher<ResponseEntity<Profile>> updateById(@PathVariable("id") String id, @RequestBody Profile profile){
        return Mono.just(profile)
                .flatMap(p ->
                        this.profileService.update(id, p.getEmail())
                )
                .map(p ->
                        ResponseEntity
                                .ok()
                                .contentType(mediaType)
                                .build()
                );
    }
}
