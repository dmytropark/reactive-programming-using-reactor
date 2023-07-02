package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieInfoServiceTest {
    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();
    MovieInfoService movieInfoService = new MovieInfoService(webClient);

    @Test
    void retrieveMovieInfoById_RestClient() {
        StepVerifier.create(movieInfoService.retrieveMovieInfoById_RestClient(1L))
                .assertNext(movieInfo -> {
                    assertEquals(1L, movieInfo.getMovieInfoId());
                    assertEquals("Batman Begins", movieInfo.getName());
                    assertEquals(2, movieInfo.getCast().size());
                })
                .verifyComplete();

    }
}