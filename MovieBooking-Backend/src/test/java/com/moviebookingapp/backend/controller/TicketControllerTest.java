package com.moviebookingapp.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebookingapp.backend.Controller.TicketController;
import com.moviebookingapp.backend.model.Ticket;
import com.moviebookingapp.backend.security.TokenHelper;
import com.moviebookingapp.backend.service.TicketService;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TicketService ticketService;

    // ---------------- BOOK TICKET ----------------

    @Test
    void book_shouldReturnOk_whenUserLoggedIn() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setMovieName("Avatar");
        ticket.setTheatreName("IMAX");
        ticket.setSeatNumber("A1,A2");
        ticket.setNumberOfTickets(2);

        try (MockedStatic<TokenHelper> tokenHelper = mockStatic(TokenHelper.class)) {

            tokenHelper.when(() -> TokenHelper.getUserFromToken("Bearer token"))
                       .thenReturn("user1");

            when(ticketService.bookTicket(eq("Avatar"), any(Ticket.class), eq("user1")))
                    .thenReturn("Ticket booked successfully");

            mockMvc.perform(post("/api/v1.0/moviebooking/Avatar/add")
                            .header("Authorization", "Bearer token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ticket)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Ticket booked successfully"));
        }
    }

    @Test
    void book_shouldReturnUnauthorized_whenNotLoggedIn() throws Exception {
        Ticket ticket = new Ticket();

        try (MockedStatic<TokenHelper> tokenHelper = mockStatic(TokenHelper.class)) {

            tokenHelper.when(() -> TokenHelper.getUserFromToken(null))
                       .thenReturn(null);

            mockMvc.perform(post("/api/v1.0/moviebooking/Avatar/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ticket)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string("Please login to book tickets"));
        }
    }

    // ---------------- VIEW TICKETS (ADMIN) ----------------

    @Test
    void viewBookedTickets_shouldReturnTickets_whenAdmin() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setSeatNumber("B1");

        try (MockedStatic<TokenHelper> tokenHelper = mockStatic(TokenHelper.class)) {

            tokenHelper.when(() -> TokenHelper.getRoleFromToken("Bearer admin"))
                       .thenReturn("ADMIN");

            when(ticketService.getTicketsByMovie("Avatar", "IMAX"))
                    .thenReturn(List.of(ticket));

            mockMvc.perform(get("/api/v1.0/moviebooking/admin/movies/Avatar/theatre/IMAX/tickets")
                            .header("Authorization", "Bearer admin"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].seatNumber").value("B1"));
        }
    }

    @Test
    void viewBookedTickets_shouldReturnForbidden_whenNotAdmin() throws Exception {
        try (MockedStatic<TokenHelper> tokenHelper = mockStatic(TokenHelper.class)) {

            tokenHelper.when(() -> TokenHelper.getRoleFromToken("Bearer user"))
                       .thenReturn("USER");

            mockMvc.perform(get("/api/v1.0/moviebooking/admin/movies/Avatar/theatre/IMAX/tickets")
                            .header("Authorization", "Bearer user"))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string("Admin access required"));
        }
    }

    // ---------------- GET USER TICKETS ----------------

    @Test
    void getMyTickets_shouldReturnTickets_whenLoggedIn() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setSeatNumber("C1");

        try (MockedStatic<TokenHelper> tokenHelper = mockStatic(TokenHelper.class)) {

            tokenHelper.when(() -> TokenHelper.getUserFromToken("Bearer token"))
                       .thenReturn("user1");

            when(ticketService.getTicketsByUser("user1"))
                    .thenReturn(List.of(ticket));

            mockMvc.perform(get("/api/v1.0/moviebooking/tickets/user")
                            .header("Authorization", "Bearer token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].seatNumber").value("C1"));
        }
    }

    @Test
    void getMyTickets_shouldReturnUnauthorized_whenNotLoggedIn() throws Exception {
        try (MockedStatic<TokenHelper> tokenHelper = mockStatic(TokenHelper.class)) {

            tokenHelper.when(() -> TokenHelper.getUserFromToken("Bearer token"))
                       .thenReturn(null);

            mockMvc.perform(get("/api/v1.0/moviebooking/tickets/user")
                            .header("Authorization", "Bearer token"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string("Please login"));
        }
    }

    // ---------------- UPDATE TICKET ----------------

    @Test
    void updateTicket_shouldReturnOk_whenAdmin() throws Exception {
        Ticket updated = new Ticket();
        updated.setSeatNumber("D1");

        try (MockedStatic<TokenHelper> tokenHelper = mockStatic(TokenHelper.class)) {

            tokenHelper.when(() -> TokenHelper.getRoleFromToken("Bearer admin"))
                       .thenReturn("ADMIN");

            when(ticketService.updateTicket(eq("123"), any(Ticket.class)))
                    .thenReturn("Ticket updated");

            mockMvc.perform(put("/api/v1.0/moviebooking/Avatar/update/123")
                            .header("Authorization", "Bearer admin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updated)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Ticket updated"));
        }
    }

    // ---------------- BOOKED SEATS ----------------

    @Test
    void getBookedSeats_shouldReturnSeatList() throws Exception {
        Ticket t1 = new Ticket();
        t1.setSeatNumber("A1,A2");

        Ticket t2 = new Ticket();
        t2.setSeatNumber("B1");

        when(ticketService.getTicketsByMovie("Avatar", "IMAX"))
                .thenReturn(List.of(t1, t2));

        mockMvc.perform(get("/api/v1.0/moviebooking/admin/movies/Avatar/IMAX/booked-seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("A1"))
                .andExpect(jsonPath("$[1]").value("A2"))
                .andExpect(jsonPath("$[2]").value("B1"));
    }
}
