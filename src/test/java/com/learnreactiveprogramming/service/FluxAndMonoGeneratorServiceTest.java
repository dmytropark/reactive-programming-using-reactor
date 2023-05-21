package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

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
        var flux = service.namesFlux_flatmap();
        StepVerifier.create(flux)
                .expectNext("A", "L", "E", "X", "B", "E", "N")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatmap_with_delay() {
        var flux = service.namesFlux_flatmap_with_delay();
        StepVerifier.create(flux)
                .expectNextCount(7)
                .verifyComplete();
    }

    @Test
    void namesFlux_concatMap_with_delay() {
        var flux = service.namesFlux_concatMap_with_delay();
        StepVerifier.create(flux)
                .expectNext("A", "L", "E", "X", "B", "E", "N")
                .verifyComplete();
    }

    @Test
    void testNamesMono_flatmap() {
        var flux = service.namesMono_flatmap(3);

        StepVerifier.create(flux)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();
    }

    @Test
    void namesMono_flatMapMany() {
        var flux = service.namesMono_flatMapMany(3);
        StepVerifier.create(flux)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();

    }

    @Test
    void namesFlux_transform() {
        var flux = service.namesFlux_transform(2);
        StepVerifier.create(flux)
                .expectNext("A", "L", "E", "X", "B", "E", "N")
                .verifyComplete();
    }

    @Test
    void namesFlux_defaultIfEmpty() {
        var flux = service.namesFlux_defaultIfEmpty(10);
        StepVerifier.create(flux)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFlux_switchIfEmpty() {
        var flux = service.namesFlux_switchIfEmpty(6);
        StepVerifier.create(flux)
                .expectNext("D", "E", "F", "A", "U", "L", "T")
                .verifyComplete();
    }

    @Test
    void concatFlux() {
        var flux = service.concatFlux();
        StepVerifier.create(flux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void concatWithFlux() {
        var flux = service.concatWithFlux();
        StepVerifier.create(flux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void concatWithMono() {
        var flux = service.concatWithMono();
        StepVerifier.create(flux)
                .expectNext("A", "B")
                .verifyComplete();
    }
}
