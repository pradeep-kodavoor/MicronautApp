package com.practise;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("${hello.controller.path}")
public class HelloWorldController {



    private final HelloWorldService service;
    private final GreetingConfig config;

    public HelloWorldController(final HelloWorldService service, GreetingConfig config) {
        this.service = service;
        this.config = config;
    }


    @Get("/")
    public String hello() {
        // return "Hello World";
        return service.sayHi();
    }

    @Get("/de")
    public String helloInDE() {
        return config.getDe();
    }

    @Get("/en")
    public String helloInEN() {
        return config.getEn();
    }
}
