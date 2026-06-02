package com.moviebookingapp.backend.Controller;

import com.moviebookingapp.backend.model.Ticket;
import com.moviebookingapp.backend.security.TokenHelper;
import com.moviebookingapp.backend.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1.0/moviebooking")
public class TicketController {

    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // Book tickets -> requires login
    @PostMapping("/{moviename}/add")
    public ResponseEntity<?> book(@PathVariable String moviename,
            @RequestBody Ticket ticket,
            @RequestHeader(value = "Authorization", required = false) String authHeader
) {
        String user = TokenHelper.getUserFromToken(authHeader);
        if (user == null)
            return ResponseEntity.status(401).body("Please login to book tickets");

        String res = ticketService.bookTicket(moviename, ticket, user);
        if (res.contains("booked"))
            return ResponseEntity.ok(res);
        return ResponseEntity.badRequest().body(res);
    }

    // View tickets for a movie (admin)
    @GetMapping("/admin/movies/{moviename}/theatre/{theatre}/tickets")
    public ResponseEntity<?> viewBookedTickets(@PathVariable String moviename,
            @PathVariable String theatre,
            @RequestHeader("Authorization") String authHeader
) {
        String role = TokenHelper.getRoleFromToken(authHeader);
        if (!"ADMIN".equalsIgnoreCase(role))
            return ResponseEntity.status(403).body("Admin access required");
        List<?> list = ticketService.getTicketsByMovie(moviename, theatre);
        return ResponseEntity.ok(list);
    }

    // Get tickets for logged-in user
    @GetMapping("/tickets/user")
    public ResponseEntity<?> getMyTickets(@RequestHeader("Authorization") String authHeader) {
        String user = TokenHelper.getUserFromToken(authHeader);
        if (user == null)
            return ResponseEntity.status(401).body("Please login");
        return ResponseEntity.ok(ticketService.getTicketsByUser(user));
    }

    // Update ticket (admin or user owning ticket) - here admin-only for status
    @PutMapping("/{moviename}/update/{ticketId}")
    public ResponseEntity<?> updateTicket(@PathVariable String moviename,
            @PathVariable String ticketId,
            @RequestBody Ticket updated,
            @RequestHeader("Authorization") String authHeader
) {
        String role = TokenHelper.getRoleFromToken(authHeader);
        if (!"ADMIN".equalsIgnoreCase(role))
            return ResponseEntity.status(403).body("Admin access required to update tickets");
        String res = ticketService.updateTicket(ticketId, updated);
        if (res.equals("Ticket updated"))
            return ResponseEntity.ok(res);
        return ResponseEntity.badRequest().body(res);
    }

    @GetMapping("/admin/movies/{movieName}/{theatreName}/booked-seats")
    public ResponseEntity<?> getBookedSeats(
        @PathVariable String movieName,
        @PathVariable String theatreName) {

        List<Ticket> tickets = ticketService.getTicketsByMovie(movieName, theatreName);
        List<String> bookedSeats = new ArrayList<>();

        for (Ticket t : tickets) {
        if (t.getSeatNumber() != null) {
            bookedSeats.addAll(Arrays.asList(t.getSeatNumber().split(",")));
        }
    }

    return ResponseEntity.ok(bookedSeats);
   }
}

