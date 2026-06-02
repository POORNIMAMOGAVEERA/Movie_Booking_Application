package com.moviebookingapp.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.moviebookingapp.backend.model.Movie;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {

    // Find movie by name (exact or partial)
    List<Movie> findByMovieNameContainingIgnoreCase(String movieName);

    // Find by both movie and theatre name (composite key logic)
    Optional<Movie> findByMovieNameAndTheatreName(String movieName, String theatreName);

}

