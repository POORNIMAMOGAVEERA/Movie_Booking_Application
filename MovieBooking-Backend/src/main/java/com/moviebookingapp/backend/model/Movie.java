package com.moviebookingapp.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "movies")
public class Movie {

    @Id
    private String id;
    private String movieName;
    private String theatreName;
    private int totalTickets;
    private int availableTickets;
    private String status; // e.g., "BOOK ASAP", "SOLD OUT", "Available"

    public Movie() {}

    public Movie(String id, String movieName, String theatreName, int totalTickets, int availableTickets, String status) {
        this.id = id;
        this.movieName = movieName;
        this.theatreName = theatreName;
        this.totalTickets = totalTickets;
        this.availableTickets = availableTickets;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getMovieName() {
        return movieName;
    }
    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getTheatreName() {
        return theatreName;
    }
    public void setTheatreName(String theatreName) {
        this.theatreName = theatreName;
    }

    public int getTotalTickets() {
        return totalTickets;
    }
    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public int getAvailableTickets() {
        return availableTickets;
    }
    public void setAvailableTickets(int availableTickets) {
        this.availableTickets = availableTickets;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
