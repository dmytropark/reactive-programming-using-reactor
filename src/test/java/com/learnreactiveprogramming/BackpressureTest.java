package com.learnreactiveprogramming;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class BackpressureTest {
    @Test
    void testBackPressure() {
        var numberRange = Flux.range(1,100).log();

        numberRange
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(3);
                    }
                    @Override
                    protected void hookOnNext(Integer value) {
                        // super.hookOnNext(value);
                        log.info("hookOnNext : {}",value);
                        if(value==2){
                            cancel();
                        }
                    }
                    @Override
                    protected void hookOnComplete() {
                        //super.hookOnComplete();
                    }
                    @Override
                    protected void hookOnError(Throwable throwable) {
                        //super.hookOnError(throwable);
                    }
                    @Override
                    protected void hookOnCancel() {
                        // super.hookOnCancel();
                        log.info("Inside OnCancel");
                    }
                });
    }

    @Test
    void testBackPressure_1() throws InterruptedException {
        var numberRange = Flux.range(1,100).log();

        CountDownLatch latch = new CountDownLatch(1);

        numberRange
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(3);
                    }
                    @Override
                    protected void hookOnNext(Integer value) {
                        // super.hookOnNext(value);
                        log.info("hookOnNext : {}",value);
                        if(value%2==0 || value <50){
                            request(3);
                        }else{
                            cancel();
                        }

                    }
                    @Override
                    protected void hookOnComplete() {
                        //super.hookOnComplete();
                    }
                    @Override
                    protected void hookOnError(Throwable throwable) {
                        //super.hookOnError(throwable);
                    }
                    @Override
                    protected void hookOnCancel() {
                        // super.hookOnCancel();
                        log.info("Inside OnCancel");
                        latch.countDown();
                    }
                });
        assertTrue(latch.await(5L, TimeUnit.SECONDS));
    }

    /**
     * if consumer (subscriber) is slower than producer (source)
     * we can use onBackpressureDrop mechanism to skip additional messages from source.
     *
     */
    @Test
    void testBackPressure_drop() throws InterruptedException {
        var numberRange = Flux.range(1,100).log();

        CountDownLatch latch = new CountDownLatch(1);

        numberRange
                .onBackpressureDrop(item -> {
                    log.info("dropped items are: {}", item);
                })
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(2);
                    }
                    @Override
                    protected void hookOnNext(Integer value) {
                        log.info("hookOnNext : {}", value);

                        if (value == 2) {
                           hookOnCancel();
                        }
                    }
                    @Override
                    protected void hookOnComplete() {
                        //super.hookOnComplete();
                    }
                    @Override
                    protected void hookOnError(Throwable throwable) {
                        //super.hookOnError(throwable);
                    }
                    @Override
                    protected void hookOnCancel() {
                        // super.hookOnCancel();
                        log.info("Inside OnCancel");
                        latch.countDown();
                    }
                });
        assertTrue(latch.await(5L, TimeUnit.SECONDS));
    }

    /**
     * We can use buffer approach when producer is too slow in comparison to subscriber (consumer),
     * so we can preload messages in advance.
     */
    @Test
    void testBackPressure_buffer() throws InterruptedException {
        var numberRange = Flux.range(1,100).log();

        CountDownLatch latch = new CountDownLatch(1);

        numberRange
                .onBackpressureBuffer(10, item -> {
                    log.info("!!! item = {}", item);
                })
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(1);
                    }
                    @Override
                    protected void hookOnNext(Integer value) {
                        log.info("hookOnNext : {}",value);

                        if (value < 3) {
                            request(1);
                        } else {
                            hookOnCancel();
                        }
                    }
                    @Override
                    protected void hookOnComplete() {
                        //super.hookOnComplete();
                    }
                    @Override
                    protected void hookOnError(Throwable throwable) {
                        //super.hookOnError(throwable);
                    }
                    @Override
                    protected void hookOnCancel() {
                        // super.hookOnCancel();
                        log.info("Inside OnCancel");
                        latch.countDown();
                    }
                });
        assertTrue(latch.await(5L, TimeUnit.SECONDS));
    }

    /**
     * Handle case when producer has more messages than consumer want to receive.
     *
     */
    @Test
    void testBackPressure_error() throws InterruptedException {
        var numberRange = Flux.range(1,100).log();

        CountDownLatch latch = new CountDownLatch(1);

        numberRange
                .onBackpressureError()
                .subscribe(new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(1);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        log.info("hookOnNext : {}", value);
                        hookOnCancel();

                    }

                    @Override
                    protected void hookOnComplete() {
                        //super.hookOnComplete();
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
                        //super.hookOnError(throwable);
                        log.error("!!!!", throwable);
                    }

                    @Override
                    protected void hookOnCancel() {
                        // super.hookOnCancel();
                        log.info("Inside OnCancel");
                        latch.countDown();
                    }
                });
        assertTrue(latch.await(5L, TimeUnit.SECONDS));
    }
}
