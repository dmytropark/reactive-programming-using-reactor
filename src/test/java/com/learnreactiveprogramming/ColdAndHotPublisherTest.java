package com.learnreactiveprogramming;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

@Slf4j
public class ColdAndHotPublisherTest {

    @Test
    void hotPublisherGeneralTest() {
        var flux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<Integer> connectableFlux = flux.publish();
        connectableFlux.connect();

        connectableFlux.subscribe(i -> log.info("Subscriber 1: " + i));
        delay(4000);
        connectableFlux.subscribe(i -> log.info("Subscriber 2: " + i));
        delay(10_000);

    }

    @Test
    void hotPublisherAutoConnectTest() {
        var flux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1));

        var connectableFlux = flux.publish().autoConnect(2);

        connectableFlux.subscribe(i -> log.info("Subscriber 1: " + i));
        delay(2000);
        connectableFlux.subscribe(i -> log.info("Subscriber 2: " + i));
        log.info("two subscribers are connected");
        delay(1000);
        connectableFlux.subscribe(i -> log.info("Subscriber 3: " + i));
        delay(10_000);

    }
}
