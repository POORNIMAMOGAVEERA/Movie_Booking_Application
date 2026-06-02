package com.moviebookingapp.backend.service;

import com.moviebookingapp.backend.model.Ticket;
import java.util.List;

public interface TicketService {
    String bookTicket(String moviename, Ticket ticket, String requesterLoginId);
    List<Ticket> getTicketsByMovie(String movieName, String theatreName);
    List<Ticket> getTicketsByUser(String userId);
    String updateTicket(String ticketId, Ticket updatedTicket);
    int getBookedTicketsCount(String movieName);

}

