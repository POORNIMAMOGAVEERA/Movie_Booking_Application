package com.moviebookingapp.backend.service;

import com.moviebookingapp.backend.model.User;
import com.moviebookingapp.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    private UserRepository userRepository;
    private UserServiceImpl userService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    private User sampleUser() {
        User u = new User();
        u.setFirstName("John");
        u.setLastName("Doe");
        u.setEmail("john@mail.com");
        u.setLoginId("john123");
        u.setPassword("pass");
        u.setConfirmPassword("pass");
        u.setContactNumber("9999999999");
        return u;
    }

    @Test
    void testRegisterSuccess() {
        User u = sampleUser();

        when(userRepository.findByLoginId("john123")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@mail.com")).thenReturn(Optional.empty());

        String result = userService.registerUser(u);

        assertEquals("Registration successful", result);
        verify(userRepository, times(1)).save(u);
    }

    @Test
    void testRegisterMissingFields() {
        User u = new User(); // empty

        String result = userService.registerUser(u);

        assertEquals("All fields are mandatory", result);
    }

    @Test
    void testRegisterLoginIdExists() {
        User u = sampleUser();

        when(userRepository.findByLoginId("john123"))
                .thenReturn(Optional.of(u));

        String result = userService.registerUser(u);

        assertEquals("Login Id already exists", result);
    }

    @Test
    void testRegisterEmailExists() {
        User u = sampleUser();

        when(userRepository.findByLoginId("john123")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@mail.com"))
                .thenReturn(Optional.of(u));

        String result = userService.registerUser(u);

        assertEquals("Email already exists", result);
    }

    @Test
    void testRegisterPasswordMismatch() {
        User u = sampleUser();
        u.setConfirmPassword("nope");

        String result = userService.registerUser(u);

        assertEquals("Password and Confirm Password must match", result);
    }

    @Test
    void testLoginSuccess() {
        User u = sampleUser();

        when(userRepository.findByLoginId("john123"))
                .thenReturn(Optional.of(u));

        String result = userService.loginUser("john123", "pass");

        assertEquals("Login successful", result);
    }

    @Test
    void testLoginFailed() {
        when(userRepository.findByLoginId("john123"))
                .thenReturn(Optional.empty());

        String result = userService.loginUser("john123", "wrong");

        assertEquals("Invalid credentials", result);
    }

    @Test
    void testForgotPasswordSelf() {
        User u = sampleUser();

        when(userRepository.findByLoginId("john123")).thenReturn(Optional.of(u));

        String result = userService.forgotPassword(
                "john123",
                "newPass",
                "john123",
                "USER"
        );

        assertEquals("Password updated successfully", result);
        verify(userRepository).save(u);
    }

    @Test
    void testForgotPasswordAdminAllowed() {
        User u = sampleUser();

        when(userRepository.findByLoginId("john123")).thenReturn(Optional.of(u));

        String result = userService.forgotPassword(
                "john123",
                "newPass",
                "admin001",
                "ADMIN"
        );

        assertEquals("Password updated successfully", result);
    }

    @Test
    void testForgotPasswordUnauthorized() {
        User u = sampleUser();

        when(userRepository.findByLoginId("john123")).thenReturn(Optional.of(u));

        String result = userService.forgotPassword(
                "john123",
                "newPass",
                "randomGuy",
                "USER"
        );

        assertEquals("Not authorized to reset password for this user", result);
    }

    @Test
    void testForgotPasswordUserNotFound() {
        when(userRepository.findByLoginId("john123"))
                .thenReturn(Optional.empty());

        String result = userService.forgotPassword(
                "john123",
                "newPass",
                "john123",
                "USER"
        );

        assertEquals("User not found", result);
    }

    @Test
    void testChangePasswordForLoggedIn() {
        User u = sampleUser();

        when(userRepository.findByLoginId("john123"))
                .thenReturn(Optional.of(u));

        userService.changePasswordForLoggedIn("john123", "changed");

        verify(userRepository).save(u);
        assertEquals("changed", u.getPassword());
    }
}
