package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;

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

    public Flux<String> namesFlux_flatmap() {
        return Flux.fromIterable( List.of("alex", "ben"))
                .log()
                .map(String::toUpperCase)
                .flatMap(this::splitString)
                .log();

    }

    public Flux<String> namesFlux_flatmap_with_delay() {
        return Flux.fromIterable( List.of("alex", "ben"))
                .map(String::toUpperCase)
                .flatMap(this::splitString_with_delay)
                .log();

    }

    public Flux<String> namesFlux_concatMap_with_delay() {
        return Flux.fromIterable( List.of("alex", "ben"))
                .map(String::toUpperCase)
                .concatMap(this::splitString_with_delay)
                .log();

    }

    private Flux<String> splitString(String name) {
       var charArray = name.split("");
       return Flux.fromArray(charArray);
    }

    private Flux<String> splitString_with_delay(String name) {
        var charArray = name.split("");
        var delay = new Random().nextInt(1000);
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(delay));
    }

}
