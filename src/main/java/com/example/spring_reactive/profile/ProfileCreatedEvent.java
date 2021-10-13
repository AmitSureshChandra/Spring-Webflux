package com.example.spring_reactive.profile;

import org.springframework.context.ApplicationEvent;

public class ProfileCreatedEvent extends ApplicationEvent {
    public ProfileCreatedEvent(Object source) {
        super(source);
    }
}
