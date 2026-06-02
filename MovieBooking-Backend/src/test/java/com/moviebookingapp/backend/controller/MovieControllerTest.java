package com.moviebookingapp.backend.controller;

import com.moviebookingapp.backend.Controller.MovieController;
import com.moviebookingapp.backend.config.KafkaProducerService;
import com.moviebookingapp.backend.model.Movie;
import com.moviebookingapp.backend.service.MovieService;
import com.moviebookingapp.backend.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MovieService movieService;

    @MockBean
    private TicketService ticketService;


    // ---------------- ADD MOVIE ----------------

    @Test
    void addMovie_shouldReturnSavedMovie() throws Exception {
        Movie movie = new Movie();
        movie.setMovieName("Inception");
        movie.setTheatreName("IMAX");
        movie.setTotalTickets(100);

        when(movieService.addMovie(any(Movie.class))).thenReturn(movie);

        mockMvc.perform(post("/api/v1.0/moviebooking/movies/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movieName").value("Inception"))
                .andExpect(jsonPath("$.theatreName").value("IMAX"))
                .andExpect(jsonPath("$.totalTickets").value(100));
    }

    // ---------------- GET ALL MOVIES ----------------

    @Test
    void getAll_shouldReturnMovieList() throws Exception {
        Movie movie = new Movie();
        movie.setMovieName("Avatar");

        when(movieService.getAllMovies()).thenReturn(List.of(movie));

        mockMvc.perform(get("/api/v1.0/moviebooking/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].movieName").value("Avatar"));
    }

    // ---------------- SEARCH MOVIE ----------------

    @Test
    void search_shouldReturnMatchingMovies() throws Exception {
        Movie movie = new Movie();
        movie.setMovieName("Batman");

        when(movieService.searchMovies("Bat")).thenReturn(List.of(movie));

        mockMvc.perform(get("/api/v1.0/moviebooking/movies/search/Bat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].movieName").value("Batman"));
    }

    // ---------------- DELETE MOVIE (SUCCESS) ----------------

    @Test
    void delete_shouldReturnOk_whenMovieDeleted() throws Exception {
        when(movieService.deleteMovie("Titanic", "1"))
                .thenReturn("Movie deleted");

        mockMvc.perform(delete("/api/v1.0/moviebooking/Titanic/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Movie deleted"));
    }

    // ---------------- DELETE MOVIE (FAILURE) ----------------

    @Test
    void delete_shouldReturnBadRequest_whenDeleteFails() throws Exception {
        when(movieService.deleteMovie("Titanic", "1"))
                .thenReturn("Movie not found");

        mockMvc.perform(delete("/api/v1.0/moviebooking/Titanic/delete/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Movie not found"));
    }

    // ---------------- REFRESH AVAILABILITY ----------------

    @Test
    void refresh_shouldReturnOk_whenUpdated() throws Exception {
        when(movieService.updateAvailabilityAfterBookings("Avatar", "IMAX"))
                .thenReturn("Updated availability");

        mockMvc.perform(put("/api/v1.0/moviebooking/admin/movies/Avatar/IMAX/refresh"))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated availability"));
    }

    // ---------------- BOOKED COUNT ----------------

    @Test
    void getBookedCount_shouldReturnCount() throws Exception {
        when(ticketService.getBookedTicketsCount("Avatar")).thenReturn(5);

        mockMvc.perform(get("/api/v1.0/moviebooking/admin/movies/Avatar/booked-count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    // ---------------- UPDATE STATUS ----------------

    @Test
    void updateStatus_shouldReturnOk_whenUpdated() throws Exception {
        Movie request = new Movie();
        request.setStatus("CLOSED");

        when(movieService.updateMovieStatus("Avatar", "CLOSED"))
                .thenReturn("Updated status");

        mockMvc.perform(put("/api/v1.0/moviebooking/Avatar/update/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated status"));
    }

    // ---------------- TEST CONFIG (Kafka Stub) ----------------

    @TestConfiguration
    static class NoKafkaConfig {

        @Bean
        KafkaProducerService kafkaProducerService() {
            return new KafkaProducerService(null) {
                @Override
                public void sendAppLog(String level, String message) {
                    // no-op: prevents Kafka calls during tests
                }
            };
        }
    }
}
