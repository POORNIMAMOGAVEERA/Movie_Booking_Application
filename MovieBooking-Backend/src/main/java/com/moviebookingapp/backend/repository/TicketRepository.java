package com.moviebookingapp.backend.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.moviebookingapp.backend.model.Ticket;

import java.util.List;

@Repository
public interface TicketRepository extends MongoRepository<Ticket, String> {

    // Find tickets by movie and theatre
    List<Ticket> findByMovieNameAndTheatreName(String movieName, String theatreName);

    // Find tickets booked by a specific user
    List<Ticket> findByUserId(String userId);

    // Count tickets by movieName
    @Aggregation(pipeline = {
            "{ $match: { movieName: ?0 } }",
            "{ $group: { _id: null, total: { $sum: '$numberOfTickets' } } }"
    })
    Integer sumTicketsByMovie(String movieName);

    void deleteByMovieName(String movieName);


}
