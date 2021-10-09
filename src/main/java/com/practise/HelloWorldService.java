package com.practise;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.http.annotation.Controller;
import io.micronaut.runtime.event.annotation.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Singleton
public class HelloWorldService {

    private static final Logger LOG = LoggerFactory.getLogger(HelloWorldService.class);

    @Property(name = "hello.service.greeting", defaultValue = "Hello there")
    private String message;

    @EventListener
    public void onStartup(StartupEvent startupEvent) {
        LOG.debug("Startup: {}", HelloWorldService.class.getSimpleName());
    }

    public String sayHi() {
        return message;
    }
}
