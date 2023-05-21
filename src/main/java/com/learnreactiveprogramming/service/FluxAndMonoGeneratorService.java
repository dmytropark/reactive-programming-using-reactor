package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

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

    public Mono<List<String>> namesMono_flatmap(int len) {
        return  Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > len)
                .flatMap(this::splitStringMono)
                .log();

    }

    public Flux<String> namesMono_flatMapMany(int len) {
        return  Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > len)
                .flatMapMany(this::splitString)
                .log();

    }

    public Flux<String> namesFlux_transform(int len) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name
                .map(String::toUpperCase)
                .filter(s -> s.length() > len);

        return Flux.fromIterable(List.of("alex", "ben"))
                .transform(filterMap)
                .flatMap(this::splitString)
                .log();
    }

    public Flux<String> namesFlux_defaultIfEmpty(int len) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name
                .map(String::toUpperCase)
                .filter(s -> s.length() > len);

        return Flux.fromIterable(List.of("alex", "ben"))
                .transform(filterMap)
                .flatMap(this::splitString)
                .defaultIfEmpty("default")
                .log();
    }

    public Flux<String> namesFlux_switchIfEmpty(int len) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name
                .map(String::toUpperCase)
                .filter(s -> s.length() > len)
                .flatMap(this::splitString);

        var defaultFlux = Flux.just("default")
                .transform(filterMap);

        return Flux.fromIterable(List.of("alex", "ben"))
                .transform(filterMap)
                .switchIfEmpty(defaultFlux)
                .log();
    }

    public Flux<String> concatFlux() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        return Flux.concat(abcFlux, defFlux);
    }

    public Flux<String> concatWithFlux() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        return abcFlux.concatWith(defFlux);
    }

    public Flux<String> concatWithMono() {
        var aMono = Mono.just("A");
        var bMono = Mono.just("B");
        return aMono.concatWith(bMono);
    }

    private Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split("");
        var charList = List.of(charArray);
        return Mono.just(charList);
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
