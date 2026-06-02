package com.moviebookingapp.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.moviebookingapp.backend.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // Find by loginId or email (for login/registration)
    Optional<User> findByLoginId(String loginId);
    Optional<User> findByEmail(String email);
}
