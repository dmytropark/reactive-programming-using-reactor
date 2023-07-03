package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class ReviewServiceTest {

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();

    ReviewService reviewService = new ReviewService(webClient);

    @Test
    void retrieveReviewsFlux_RestClient() {
        long movieInfoId = 1L;
        StepVerifier.create(reviewService.reviewReviewFlux_RestClient(movieInfoId))
                .assertNext(review -> {
                    assertEquals(1L, review.getMovieInfoId());
                    assertEquals("Nolan is the real superhero", review.getComment());
                })
                .verifyComplete();
    }

    @Test
    void retrieveReviews() {
    }

    @Test
    void retrieveReviewsFlux() {
    }
}