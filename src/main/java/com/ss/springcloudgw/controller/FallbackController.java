package com.ss.springcloudgw.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @RequestMapping("/fallback")
    public Mono<String> fallback() {

        System.out.println("fallbackkkkkk");
        return Mono.just("fallback");
    }
}
