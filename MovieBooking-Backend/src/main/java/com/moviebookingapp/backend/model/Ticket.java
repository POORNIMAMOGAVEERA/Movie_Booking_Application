package com.moviebookingapp.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tickets")
public class Ticket {

    @Id
    private String id;
    private String movieName;
    private String theatreName;
    private int numberOfTickets;
    private String seatNumber; // e.g., "A1,A2"
    private String userId; // loginId of the booking user

    public Ticket() {}

    public Ticket(String id, String movieName, String theatreName, int numberOfTickets, String seatNumber, String userId) {
        this.id = id;
        this.movieName = movieName;
        this.theatreName = theatreName;
        this.numberOfTickets = numberOfTickets;
        this.seatNumber = seatNumber;
        this.userId = userId;
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

    public int getNumberOfTickets() {
        return numberOfTickets;
    }
    public void setNumberOfTickets(int numberOfTickets) {
        this.numberOfTickets = numberOfTickets;
    }

    public String getSeatNumber() {
        return seatNumber;
    }
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
