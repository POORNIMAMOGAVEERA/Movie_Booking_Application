package com.moviebookingapp.backend.service;

import com.moviebookingapp.backend.config.KafkaProducerService;
import com.moviebookingapp.backend.model.Movie;
import com.moviebookingapp.backend.model.Ticket;
import com.moviebookingapp.backend.repository.MovieRepository;
import com.moviebookingapp.backend.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final MovieRepository movieRepository;
    private final MovieService movieService; // to update status after booking
    private KafkaProducerService kafkaProducer;

    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository,
            MovieRepository movieRepository,
            MovieService movieService,
            KafkaProducerService kafkaProducer) {
        this.ticketRepository = ticketRepository;
        this.movieRepository = movieRepository;
        this.movieService = movieService;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public String bookTicket(String moviename, Ticket ticket, String requesterLoginId) {
        // ensure movie exists for the given theater
        Optional<Movie> mOpt = movieRepository.findByMovieNameAndTheatreName(ticket.getMovieName(),
                ticket.getTheatreName());
        if (!mOpt.isPresent()) {
            return "Movie or theatre not found";
        }

        Movie movie = mOpt.get();
        if (movie.getAvailableTickets() < ticket.getNumberOfTickets()) {
            return "Not enough tickets available";
        }
        
        // set userId to requester if not provided (secure)
        if (ticket.getUserId() == null || ticket.getUserId().trim().isEmpty()) {
            ticket.setUserId(requesterLoginId);
        }

        // save ticket and update movie availability
        ticketRepository.save(ticket);
        String key = ticket.getUserId();
        String payload = ticket.getMovieName() + "|" + ticket.getTheatreName(); // simple payload for consumer
        if (kafkaProducer != null) {
        kafkaProducer.sendTicketUpdate(key, payload);

        // // also send an app log message
        kafkaProducer.sendAppLog("INFO", "User " + key + " booked " + ticket.getNumberOfTickets() +
                " tickets for " + ticket.getMovieName() + "@" + ticket.getTheatreName());}
        movieService.updateAvailabilityAfterBookings(ticket.getMovieName(), ticket.getTheatreName());
        return "Ticket booked successfully";
    }

    @Override
    public List<Ticket> getTicketsByMovie(String movieName, String theatreName) {
        return ticketRepository.findByMovieNameAndTheatreName(movieName, theatreName);
    }

    @Override
    public List<Ticket> getTicketsByUser(String userId) {
        return ticketRepository.findByUserId(userId);
    }

    @Override
    public String updateTicket(String ticketId, Ticket updatedTicket) {
        Optional<Ticket> e = ticketRepository.findById(ticketId);
        if (!e.isPresent())
            return "Ticket not found";

        Ticket t = e.get();
        // only allow updating seatNumber & numberOfTickets (for example)
        t.setSeatNumber(updatedTicket.getSeatNumber());
        t.setNumberOfTickets(updatedTicket.getNumberOfTickets());
        ticketRepository.save(t);

        // update movie availability after change
        movieService.updateAvailabilityAfterBookings(t.getMovieName(), t.getTheatreName());
        return "Ticket updated";
    }

    @Override
    public int getBookedTicketsCount(String movieName) {
        Integer total = ticketRepository.sumTicketsByMovie(movieName);
        return total != null ? total : 0;
    }

}
