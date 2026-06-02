package com.moviebookingapp.backend.service;

import com.moviebookingapp.backend.model.Movie;
import java.util.List;

public interface MovieService {
    Movie addMovie(Movie movie);
    List<Movie> getAllMovies();
    List<Movie> searchMovies(String moviename);
    String deleteMovie(String moviename, String id);
    String updateAvailabilityAfterBookings(String movieName, String theatreName); // recompute status
    String updateMovieStatus(String movieName, String status);
    
}
