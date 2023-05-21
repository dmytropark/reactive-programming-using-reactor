package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public class FluxAndMonoGeneratorServiceTest {

    private  FluxAndMonoGeneratorService service = new FluxAndMonoGeneratorService();

    @Test
    void testFlux() {
        service.namesFlux()
                .subscribe(name -> System.out.println("Name is: " + name));
        Assertions.assertTrue(true);
    }

    @Test
    void testMono() {
        service.nameMono()
                .subscribe(name -> System.out.println("Mono Name is: " + name));
        Assertions.assertTrue(true);
    }

    @Test
    void testMonoAdvanced() {
        var nameMono = service.nameMono();
        StepVerifier.create(nameMono)
                .expectNext("alex")
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void namesFluxMap() {
        var flux = service.namesFluxMap();

        StepVerifier.create(flux)
                .expectNext("ALEX", "BEN", "CHLOE")
                .verifyComplete();
    }

    @Test
    void namesMono_map_filter() {
        var mono = service.namesMono_map_filter(2);
        StepVerifier.create(mono)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void namesMono_map_filter_2() {
        var mono = service.namesMono_map_filter(10);
        StepVerifier.create(mono)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void namesMono_flatmap() {
        var flux = service.namesMono_flatmap();
        StepVerifier.create(flux)
                .expectNext("A", "L", "E", "X", "B", "E", "N")
                .verifyComplete();

    }
}
