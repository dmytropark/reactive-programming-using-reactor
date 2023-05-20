package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux.fromIterable( List.of("alex", "ben", "chloe"));
    }

    public Mono<String> nameMono() {
        return Mono.just("Daria");
    }
}
