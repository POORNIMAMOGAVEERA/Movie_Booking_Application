package com.moviebookingapp.backend.service;

import com.moviebookingapp.backend.model.Movie;
import com.moviebookingapp.backend.model.Ticket;
import com.moviebookingapp.backend.repository.MovieRepository;
import com.moviebookingapp.backend.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final TicketRepository ticketRepository;

    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository, TicketRepository ticketRepository) {
        this.movieRepository = movieRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Movie addMovie(Movie movie) {
        // optional: validate fields
        if (movie.getAvailableTickets() > movie.getTotalTickets()) {
            movie.setAvailableTickets(movie.getTotalTickets());
        }
        if (movie.getStatus() == null)
            movie.setStatus("BOOK ASAP");
        return movieRepository.save(movie);
    }

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public List<Movie> searchMovies(String moviename) {
        return movieRepository.findByMovieNameContainingIgnoreCase(moviename);
    }

    @Override
    public String deleteMovie(String moviename, String id) {
        if (!movieRepository.existsById(id))
            return "Movie not found";

        ticketRepository.deleteByMovieName(moviename);   
        movieRepository.deleteById(id);
        return "Movie and related tickets deleted successfully";
    }

    /**
     * Recompute available tickets and update status based on tickets collection.
     * Called after bookings or when admin wants to refresh status.
     */
    @Override
    public String updateAvailabilityAfterBookings(String movieName, String theatreName) {

        Optional<Movie> opt = movieRepository.findByMovieNameAndTheatreName(movieName, theatreName);
        if (!opt.isPresent()) {
            return "Movie not found";
        }

        Movie movie = opt.get();

        int total = movie.getTotalTickets();

        // Sum of all booked seats from Tickets table
        int booked = ticketRepository
                .findByMovieNameAndTheatreName(movieName, theatreName)
                .stream()
                .mapToInt(Ticket::getNumberOfTickets)
                .sum();

        int available = total - booked;
        movie.setAvailableTickets(Math.max(available, 0));

        if (available <= 0) {
            movie.setStatus("SOLD OUT");
        } else if (booked >= (total / 2)) {
            movie.setStatus("BOOK ASAP");
        } else {
            movie.setStatus("Available");
        }

        movieRepository.save(movie);
        return "Updated availability and status";
    }

    @Override
    public String updateMovieStatus(String movieName, String status) {
        List<Movie> movies = movieRepository.findByMovieNameContainingIgnoreCase(movieName);
        if (movies.isEmpty()) {
            return "Movie not found";
        }

        for (Movie m : movies) {
            m.setStatus(status);
            movieRepository.save(m);
        }

        return "Updated status for movie: " + movieName;
    }

}
