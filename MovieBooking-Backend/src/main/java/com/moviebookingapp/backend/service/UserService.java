package com.moviebookingapp.backend.service;

import com.moviebookingapp.backend.model.User;


public interface UserService {
    String registerUser(User user);
    String loginUser(String loginId, String password);
    String forgotPassword(String username, String newPassword, String requesterLoginId, String requesterRole);
    void changePasswordForLoggedIn(String username, String newPassword); // optional helper
}
