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
}
