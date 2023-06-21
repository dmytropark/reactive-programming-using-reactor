package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.MovieException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MovieReactiveServiceTest {
    @Mock
    MovieInfoService movieInfoService;
    @Mock
    ReviewService reviewService;
    @InjectMocks
    MovieReactiveService movieReactiveService;

    @Test
    void test_failed_first_service() {
        var originMovieInfoService = new MovieInfoService();

        Mockito.when(movieInfoService.retrieveMoviesFlux())
                .thenReturn(
                        originMovieInfoService.retrieveMoviesFlux()
                                .concatWith(Flux.error(new RuntimeException("!!!Second Exception")))
                );

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong())).thenCallRealMethod();

        StepVerifier.create(movieReactiveService.getAllMovies())
                .expectNextCount(3)
                .expectError(MovieException.class)
                .verify();

    }

    @Test
    void test_failed_second_service() {
        Mockito.when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new RuntimeException("!!!!Exception from second service"));
        ;

        StepVerifier.create(movieReactiveService.getAllMovies())
                .expectError(MovieException.class)
                .verify();
    }

    @Test
    void getAllMoviesWithRetry() {
        Mockito.when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new RuntimeException("!!!!Exception from second service"));
        ;

        StepVerifier.create(movieReactiveService.getAllMoviesWithRetry(3))
                .expectErrorMessage("!!!!Exception from second service")
                .verify();

        verify(reviewService, times(4))
                .retrieveReviewsFlux(isA(Long.class));
    }
}
