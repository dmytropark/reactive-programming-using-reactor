package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public class FluxAndMonoGeneratorServiceTest {

    @Test
    void testFlux() {
        FluxAndMonoGeneratorService service = new FluxAndMonoGeneratorService();
        service.namesFlux().subscribe(name -> System.out.println("Name is: " + name));
        Assertions.assertTrue(true);
    }

    @Test
    void testMono() {
        FluxAndMonoGeneratorService service = new FluxAndMonoGeneratorService();
        service.nameMono().subscribe(name -> System.out.println("Mono Name is: " + name));
        Assertions.assertTrue(true);
    }

    @Test
    void testMonoAdvanced() {
        FluxAndMonoGeneratorService service = new FluxAndMonoGeneratorService();

        var nameMono = service.nameMono();
        StepVerifier.create(nameMono)
                .expectNext("alex")
                .expectNextCount(0)
                .verifyComplete();
    }
}
