package com.learnreactiveprogramming.service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

@Slf4j
public class FluxAndMonoSchedulersService {

    static List<String> namesList = List.of("alex", "ben", "chloe");
    static List<String> namesList1 = List.of("adam", "jill", "jack");

    /**
     * When to use publishOn operator:
     * 1) never block the thread in reactive programming
     * 2) Blocking operation in the reactive pipeline should be performed after publishOn operator
     * 3) The thread of execution is determined by the Scheduler that passed to it.
     *
     * Note: publishOn or subscribeOn just switch the execution thread! It will not do messages in parallel!
     *
     * "publishOn" - applies on downstream.
     * "subscribeOn" - the same but applies on upstream.
     */
    public Flux<String> explore_publishOn() {
        var namesFlux = Flux.fromIterable(namesList)
                .publishOn(Schedulers.parallel())
                .map(this::upperCase)
                .log();

        var namesFlux1 = Flux.fromIterable(namesList1)
                .publishOn(Schedulers.parallel())
                .map(this::upperCase)
                .log();

        return namesFlux.mergeWith(namesFlux1);
//        return namesFlux.concatWith(namesFlux1);
    }

    /**
     *  It will do messages in parallel!
     */
    public ParallelFlux<String> explore_parallel() {
        var noOfCores = Runtime.getRuntime().availableProcessors();
        log.info("noOfCores = {}", noOfCores);
        return Flux.fromIterable(namesList)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(this::upperCase)
                .log();
    }

    private String upperCase(String name) {
        delay(1000);
        return name.toUpperCase();
    }

}
