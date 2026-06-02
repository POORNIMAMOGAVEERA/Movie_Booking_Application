package com.moviebookingapp.backend.service;

import com.moviebookingapp.backend.model.Movie;
import com.moviebookingapp.backend.model.Ticket;
import com.moviebookingapp.backend.repository.MovieRepository;
import com.moviebookingapp.backend.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MovieServiceTest {

    private MovieRepository movieRepo;
    private TicketRepository ticketRepo;
    private MovieServiceImpl movieService;

    @BeforeEach
    void setup() {
        movieRepo = Mockito.mock(MovieRepository.class);
        ticketRepo = Mockito.mock(TicketRepository.class);
        movieService = new MovieServiceImpl(movieRepo, ticketRepo);
    }

    @Test
    void testAddMovieSetsDefaultStatus() {
        Movie movie = new Movie(null, "Avatar", "PVR", 100, 100, null);

        when(movieRepo.save(any(Movie.class))).thenReturn(movie);

        Movie saved = movieService.addMovie(movie);

        assertEquals("BOOK ASAP", saved.getStatus());
    }

    @Test
    void testSearchMovies() {
        List<Movie> mockList = Arrays.asList(new Movie("1", "Avatar", "PVR", 100, 90, "Available"));

        when(movieRepo.findByMovieNameContainingIgnoreCase("ava")).thenReturn(mockList);

        List<Movie> result = movieService.searchMovies("ava");

        assertEquals(1, result.size());
        assertEquals("Avatar", result.get(0).getMovieName());
    }

    @Test
    void testDeleteMovieDeletesTicketsAlso() {
        when(movieRepo.existsById("123")).thenReturn(true);

        String result = movieService.deleteMovie("Avatar", "123");

        verify(ticketRepo, times(1)).deleteByMovieName("Avatar");
        verify(movieRepo, times(1)).deleteById("123");

        assertEquals("Movie and related tickets deleted successfully", result);
    }

    @Test
    void testUpdateAvailabilityAfterBookings() {
        Movie movie = new Movie("1", "Avatar", "PVR", 100, 100, "Available");

        when(movieRepo.findByMovieNameAndTheatreName("Avatar", "PVR"))
                .thenReturn(Optional.of(movie));

        Ticket t1 = new Ticket();
        t1.setNumberOfTickets(40);

        Ticket t2 = new Ticket();
        t2.setNumberOfTickets(20);

        when(ticketRepo.findByMovieNameAndTheatreName("Avatar", "PVR"))
                .thenReturn(Arrays.asList(t1, t2));

        String result = movieService.updateAvailabilityAfterBookings("Avatar", "PVR");

        assertEquals("Updated availability and status", result);
        assertEquals(40, movie.getAvailableTickets());
        assertEquals("BOOK ASAP", movie.getStatus());
    }
}
