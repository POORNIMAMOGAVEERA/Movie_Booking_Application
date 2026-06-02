package com.moviebookingapp.backend.Controller;

import com.moviebookingapp.backend.config.KafkaProducerService;
import com.moviebookingapp.backend.model.Movie;
import com.moviebookingapp.backend.service.MovieService;
import com.moviebookingapp.backend.service.TicketService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/moviebooking")
public class MovieController {

    private final MovieService movieService;
    private final TicketService ticketService;
    private final KafkaProducerService kafkaProducer;

    @Autowired
    public MovieController(MovieService movieService, KafkaProducerService kafkaProducer, TicketService ticketService) {
        this.movieService = movieService;
        this.kafkaProducer = kafkaProducer;
        this.ticketService = ticketService;
    }

    // Add movie (could be admin-only; here allowed for simplicity)
    @PostMapping("/movies/add")
    public ResponseEntity<?> addMovie(@RequestBody Movie movie) {
        Movie saved = movieService.addMovie(movie);
        kafkaProducer.sendAppLog("INFO",
                String.format("MOVIE ADD | %s | movie=%s | theatre=%s | total=%d",
                        Instant.now().toString(), saved.getMovieName(), saved.getTheatreName(),
                        saved.getTotalTickets()));

        return ResponseEntity.ok(saved);
    }

    // View all movies
    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getAll() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    // Search movies (partial)
    @GetMapping("/movies/search/{moviename}")
    public ResponseEntity<List<Movie>> search(@PathVariable String moviename) {
        return ResponseEntity.ok(movieService.searchMovies(moviename));
    }

    // Delete movie by id (admin ideally)
    @DeleteMapping("/{moviename}/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String moviename, @PathVariable String id,
            @RequestHeader(value = "X-Requester", required = false) String requester) {
        // requester header optional; admin check can be added
        String res = movieService.deleteMovie(moviename, id);
        kafkaProducer.sendAppLog(res.equals("Movie deleted") ? "INFO" : "WARN",
                String.format("MOVIE DELETE | %s | movie=%s | id=%s | result=%s",
                        Instant.now().toString(), moviename, id, res));

        if (res.equals("Movie deleted"))
            return ResponseEntity.ok(res);
        return ResponseEntity.badRequest().body(res);
    }

    // Admin endpoint to refresh availability and status
    @PutMapping("/admin/movies/{moviename}/{theatre}/refresh")
    public ResponseEntity<?> refresh(@PathVariable String moviename, @PathVariable String theatre) {
        String res = movieService.updateAvailabilityAfterBookings(moviename, theatre);
        if (res.startsWith("Updated"))
            return ResponseEntity.ok(res);
        return ResponseEntity.badRequest().body(res);
    }

    @GetMapping("/admin/movies/{movieName}/booked-count")
    public ResponseEntity<?> getBookedCount(@PathVariable String movieName) {
        int count = ticketService.getBookedTicketsCount(movieName);
        return ResponseEntity.ok(count);
    }

    // NEW: Update movie status
    @PutMapping("/{moviename}/update/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable String moviename,
            @RequestBody Movie requestBody) {

        String newStatus = requestBody.getStatus();
        String result = movieService.updateMovieStatus(moviename, newStatus);

        kafkaProducer.sendAppLog(
                result.startsWith("Updated") ? "INFO" : "WARN",
                String.format("STATUS UPDATE | %s | movie=%s | status=%s | result=%s",
                        Instant.now().toString(), moviename, newStatus, result));

        if (result.startsWith("Updated"))
            return ResponseEntity.ok(result);

        return ResponseEntity.badRequest().body(result);
    }

}
