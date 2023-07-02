package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.exception.MovieException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@AllArgsConstructor
@Slf4j
public class MovieReactiveService {

    private final MovieInfoService movieInfoService;
    private final ReviewService reviewService;
    private RevenueService revenueService;

    public Flux<Movie> getAllMovies() {
        return movieInfoService.retrieveMoviesFlux()
                .flatMap(movieInfo -> reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                        .collectList()
                        .map(reviewList -> new Movie(movieInfo, reviewList)))
                .onErrorMap(MovieException::new)
                .log();
    }

    public Flux<Movie> getAllMoviesWithRetry(int numberOfTimes) {
        return movieInfoService.retrieveMoviesFlux()
                .doOnNext(item -> log.info("!!! item = " + item))
                .flatMap(movieInfo -> reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                        .collectList()
                        .map(reviewList -> new Movie(movieInfo, reviewList)))
                .onErrorMap(ex -> {
                    log.error("!!! on error map handler is here");
                    return new MovieException(ex.getMessage());
                })
                .retry(numberOfTimes)
                .log();
    }

    public Mono<Movie> getMovieById(long movieId) {
        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewsMono = reviewService.retrieveReviewsFlux(movieId)
                .collectList();

        return movieInfoMono.zipWith(reviewsMono, Movie::new);
    }

    public Mono<Movie> getMovieByIdV2(long movieId) {
        return movieInfoService.retrieveMovieInfoMonoUsingId(movieId)
                .flatMap(movieInfo -> reviewService.retrieveReviewsFlux(movieId)
                        .collectList()
                        .map(reviews -> new Movie(movieInfo, reviews)));
    }

    public Mono<Movie> getMovieById_withRevenue(long movieId) {
        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewsMono = reviewService.retrieveReviewsFlux(movieId)
                .collectList();

        var revenueMono = Mono.fromCallable(() ->  revenueService.getRevenue(movieId))
                .subscribeOn(Schedulers.boundedElastic());

        return movieInfoMono.zipWith(reviewsMono, Movie::new)
                .zipWith(revenueMono, (movie, revenue) -> {
                    movie.setRevenue(revenue);
                    return movie;
                });
    }

}
