package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoSchedulersServiceTest {

    @Test
    void explore_publishOn() {
        var flux = new FluxAndMonoSchedulersService().explore_publishOn();

        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void explore_parallel() {
        var flux = new FluxAndMonoSchedulersService().explore_parallel();

        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }
}