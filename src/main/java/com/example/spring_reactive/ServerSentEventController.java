package com.example.spring_reactive;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_reactive.profile.ProfileCreatedEvent;
import com.example.spring_reactive.websocket.ProfileCreatedEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ServerSentEventController {

	private final Flux<ProfileCreatedEvent> events;
	private final ObjectMapper objectMapper;
	
	public ServerSentEventController(ProfileCreatedEventPublisher createdEventPublisher, ObjectMapper objectMapper) {
		super();
		this.events = Flux.create(createdEventPublisher).share();
		this.objectMapper = objectMapper;
	}
	
	@GetMapping(path = "/sse/profiles", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> profiles(){
		return this.events.map(e -> {
			
			try {
				System.out.println(objectMapper.writeValueAsBytes(e) + "\n\n");
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				return objectMapper.writeValueAsString(e) + "\n\n";
			}catch (Exception error) {
				throw new RuntimeException();
			}
		});
	}
}
