package com.moviebookingapp.backend.config;

import com.moviebookingapp.backend.service.MovieService;
import com.moviebookingapp.backend.repository.TicketRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final MovieService movieService;

    @Autowired
    public KafkaConsumerService(MovieService movieService, TicketRepository ticketRepository) {
        this.movieService = movieService;
    }

    // Admin consumer: receives ticket update messages and triggers recompute
    @KafkaListener(topics = "ticket-updates", groupId = "moviebooking-group")
    public void onTicketUpdate(String message) {
        // message format we send: "movieName|theatreName"
        try {
            String[] parts = message.split("\\|");
            if (parts.length >= 2) {
                String movieName = parts[0];
                String theatreName = parts[1];
                // recompute and update movie availability/status using service method
                movieService.updateAvailabilityAfterBookings(movieName, theatreName);
                System.out.println("[KafkaConsumer] processed ticket update for " + movieName + " / " + theatreName);
            } else {
                System.out.println("[KafkaConsumer] malformed ticket-update: " + message);
            }
        } catch (Exception e) {
            System.err.println("[KafkaConsumer] error processing message: " + e.getMessage());
        }
    }

    // Simple log consumer for demonstration: prints logs to console
    @KafkaListener(topics = "app-logs", groupId = "moviebooking-group")
    public void onAppLog(String message) {
        System.out.println("[AppLog][Kafka] " + message);
    }
}
