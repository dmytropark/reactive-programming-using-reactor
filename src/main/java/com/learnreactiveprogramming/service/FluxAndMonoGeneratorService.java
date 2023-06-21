package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.ReactorException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

@Slf4j
public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux.fromIterable( List.of("alex", "ben", "chloe"));
    }

    public Mono<String> nameMono() {
        return Mono.just("alex");
    }

    public Flux<String> namesFluxMap() {
        return Flux.fromIterable( List.of("alex", "ben", "chloe"))
                .doOnNext(item -> System.out.println("1 My item = " + item))
                .log()
                .map(String::toUpperCase)
                .doOnNext(item -> System.out.println("2 My item = " + item));
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

    public Flux<String> mergeFlux() {
        var abcFlux = Flux.just("A", "B", "C")
                .doOnNext(item -> System.out.println("1 My item = " + item))
                .doFinally(s -> System.out.println("1 inside doFinally = " + s))
                .log()
                .delayElements(Duration.ofMillis(100))
                .doOnNext(item -> System.out.println("2 My item = " + item))
                .doFinally(s -> System.out.println("2 inside doFinally = " + s))
                .log();

        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(120));

        return Flux.merge(abcFlux, defFlux);
    }

    public Flux<String> concatWithMono() {
        var aMono = Mono.just("A");
        var bMono = Mono.just("B");
        return aMono.concatWith(bMono);
    }

    public Flux<String> exception_flux() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .log();
    }

    public Flux<String> exception_onErrorReturn() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IllegalStateException("Exception Occurred")))
                .onErrorReturn("D")
                .log();
    }

    public Flux<String> exception_onErrorResume(Exception e) {
        var recoveryFlux = Flux.just("D", "E", "F");

        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(e))
                .onErrorResume(ex -> {
                    log.error("Exception is ", ex);

                    if (e instanceof IllegalStateException) {
                        return recoveryFlux;
                    } else {
                        return Flux.error(e);
                    }
                })
                .log();
    }

    public Flux<String> exception_onErrorContinue() {
        return Flux.just("A", "B", "C")
                .map(name -> {
                    if(name.equals("B")) {
                        throw new IllegalStateException("Exception Occurred");
                    }

                    return name;
                })
                .concatWith(Flux.just("D"))
                .onErrorContinue((ex, name) -> {
                    log.error("Error is", ex);
                    log.info("Name is {}", name);
                })
                .log();
    }

    public Flux<String> exception_onErrorMap() {
        return Flux.just("A", "B", "C")
                .map(name -> {
                    if (name.equals("B")) {
                        throw new IllegalStateException("!!!Exception occured!!!");
                    }

                    return name;
                })
                .concatWith(Flux.just("D"))
                .onErrorMap(ex -> {
                    log.error("Exception is ", ex);
                    return new ReactorException(ex, ex.getMessage());
                })
                .log();
    }

    public Mono<Object> exception_mono_onErrorMap(Exception e) {
        return Mono.just("B")
                .flatMap(item -> Mono.error(e))
                .onErrorMap(ex -> new ReactorException(ex, "Exc occurred"));
    }

    public Mono<String> exception_mono_onErrorContinue(String input) {
        return Mono.just(input)
                .map(item -> {
                    if (item.equals("abc")) {
                        throw new RuntimeException("exception for input is =" + item);
                    }

                    return item;
                })
                .onErrorContinue((ex, item) -> {
                    log.error("continue on exception for item = " + item, ex);
                });
    }

    public Flux<String> exception_doOnError() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IllegalStateException("Exception occured")))
                .doOnError(ex -> {
                    log.error("Exception is ", ex);
                });
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
