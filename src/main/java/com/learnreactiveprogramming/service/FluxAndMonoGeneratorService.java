package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux.fromIterable( List.of("alex", "ben", "chloe"));
    }

    public Mono<String> nameMono() {
        return Mono.just("alex");
    }

    public Flux<String> namesFluxMap() {
        return Flux.fromIterable( List.of("alex", "ben", "chloe"))
                .log()
                .map(String::toUpperCase);
    }

    public Mono<String> namesMono_map_filter(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(item -> item.length() < stringLength);
    }

    public Flux<String> namesMono_flatmap() {
        return Flux.fromIterable( List.of("alex", "ben"))
                .log()
                .map(String::toUpperCase)
                .flatMap(this::splitString)
                .log();

    }

    private Flux<String> splitString(String name) {
       var charArray = name.split("");
       return Flux.fromArray(charArray);
    }

}
