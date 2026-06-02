package com.moviebookingapp.backend.service;

import com.moviebookingapp.backend.model.User;
import com.moviebookingapp.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // constructor injection (rubric)
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String registerUser(User user) {
        // 4. All details mandatory
        if (isNullOrEmpty(user.getFirstName()) ||
            isNullOrEmpty(user.getLastName()) ||
            isNullOrEmpty(user.getEmail()) ||
            isNullOrEmpty(user.getLoginId()) ||
            isNullOrEmpty(user.getPassword()) ||
            isNullOrEmpty(user.getConfirmPassword()) ||
            isNullOrEmpty(user.getContactNumber())) {
            return "All fields are mandatory";
        }

        // 5. Unique checks
        if (userRepository.findByLoginId(user.getLoginId()).isPresent()) {
            return "Login Id already exists";
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Email already exists";
        }

        // 6. Password match
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            return "Password and Confirm Password must match";
        }

        // persist
        userRepository.save(user);
        return "Registration successful";
    }

    @Override
    public String loginUser(String loginId, String password) {
        Optional<User> u = userRepository.findByLoginId(loginId);
        if (u.isPresent() && u.get().getPassword().equals(password)) {
            return "Login successful";
        }
        return "Invalid credentials";
    }

    /**
     * Reset password (acceptance: a logged-in user can reset their password).
     * We require requester to be either same user or admin (caller provides requesterLoginId/role).
     */
    @Override
    public String forgotPassword(String username, String newPassword, String requesterLoginId, String requesterRole) {
        Optional<User> target = userRepository.findByLoginId(username);
        if (!target.isPresent()) {
            return "User not found";
        }
        // allow if requester is same user or admin
        if (!requesterLoginId.equals(username) && !"ADMIN".equalsIgnoreCase(requesterRole)) {
            return "Not authorized to reset password for this user";
        }
        User u = target.get();
        u.setPassword(newPassword);
        u.setConfirmPassword(newPassword);
        userRepository.save(u);
        return "Password updated successfully";
    }

    @Override
    public void changePasswordForLoggedIn(String username, String newPassword) {
        Optional<User> target = userRepository.findByLoginId(username);
        if (target.isPresent()) {
            User u = target.get();
            u.setPassword(newPassword);
            u.setConfirmPassword(newPassword);
            userRepository.save(u);
        }
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
