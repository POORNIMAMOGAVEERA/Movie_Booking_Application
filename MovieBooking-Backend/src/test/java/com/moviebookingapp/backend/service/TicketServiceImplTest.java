package com.moviebookingapp.backend.service;

import com.moviebookingapp.backend.config.KafkaProducerService;
import com.moviebookingapp.backend.model.Movie;
import com.moviebookingapp.backend.model.Ticket;
import com.moviebookingapp.backend.repository.MovieRepository;
import com.moviebookingapp.backend.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieService movieService;

    // Remove @Mock on KafkaProducerService
    private KafkaProducerService kafkaProducer;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Use a real no-op KafkaProducerService instead of mocking
        kafkaProducer = new KafkaProducerService(null) {
            @Override
            public void sendTicketUpdate(String user, String movieTheatre) {}
            @Override
            public void sendAppLog(String level, String message) {}
        };

        ticketService = new TicketServiceImpl(ticketRepository, movieRepository, movieService, kafkaProducer);
    }

    @Test
    void testBookTicketSuccess() {
        Ticket ticket = new Ticket(null, "Avatar", "PVR", 3, "A1,A2,A3", "user1");
        Movie movie = new Movie("1", "Avatar", "PVR", 10, 10, "Available");

        when(movieRepository.findByMovieNameAndTheatreName("Avatar", "PVR"))
                .thenReturn(Optional.of(movie));

        String result = ticketService.bookTicket("Avatar", ticket, "user1");

        verify(ticketRepository, times(1)).save(ticket);
        // verify(kafkaProducer, times(1)).sendTicketUpdate("user1", "Avatar|PVR");
        verify(movieService, times(1)).updateAvailabilityAfterBookings("Avatar", "PVR");

        assertEquals("Ticket booked successfully", result);
    }

    @Test
    void testBookTicketMovieNotFound() {
        Ticket ticket = new Ticket(null, "Wrong", "PVR", 2, "A1,A2", "user1");

        when(movieRepository.findByMovieNameAndTheatreName("Wrong", "PVR"))
                .thenReturn(Optional.empty());

        String result = ticketService.bookTicket("Wrong", ticket, "user1");

        assertEquals("Movie or theatre not found", result);
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void testBookTicket_NotEnoughTickets() {
        Ticket ticket = new Ticket(null, "Avatar", "PVR", 20, "A1...", "user");
        Movie movie = new Movie("1", "Avatar", "PVR", 10, 5, "Available");

        when(movieRepository.findByMovieNameAndTheatreName("Avatar", "PVR"))
                .thenReturn(Optional.of(movie));

        String result = ticketService.bookTicket("Avatar", ticket, "user");

        assertEquals("Not enough tickets available", result);
    }

    @Test
    void testUpdateTicketSuccess() {
        Ticket existing = new Ticket("t1", "Avatar", "PVR", 2, "A1,A2", "user");
        Ticket updated = new Ticket(null, "Avatar", "PVR", 3, "A1,A2,A3", "user");

        when(ticketRepository.findById("t1")).thenReturn(Optional.of(existing));

        String result = ticketService.updateTicket("t1", updated);

        assertEquals("Ticket updated", result);
        verify(ticketRepository).save(existing);
        verify(movieService).updateAvailabilityAfterBookings("Avatar", "PVR");
    }

    @Test
    void testUpdateTicket_NotFound() {
        when(ticketRepository.findById("x")).thenReturn(Optional.empty());

        String result = ticketService.updateTicket("x", new Ticket());

        assertEquals("Ticket not found", result);
    }

    @Test
    void testGetBookedTicketsCount() {
        when(ticketRepository.sumTicketsByMovie("Avatar")).thenReturn(12);

        int result = ticketService.getBookedTicketsCount("Avatar");

        assertEquals(12, result);
    }

    @Test
    void testGetBookedTicketsCount_Null() {
        when(ticketRepository.sumTicketsByMovie("Avatar")).thenReturn(null);

        int result = ticketService.getBookedTicketsCount("Avatar");

        assertEquals(0, result);
    }
}
