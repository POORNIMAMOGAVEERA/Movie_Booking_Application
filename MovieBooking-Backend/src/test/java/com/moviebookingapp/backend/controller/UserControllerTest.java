package com.moviebookingapp.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebookingapp.backend.Controller.UserController;
import com.moviebookingapp.backend.config.KafkaProducerService;
import com.moviebookingapp.backend.model.User;
import com.moviebookingapp.backend.repository.UserRepository;
import com.moviebookingapp.backend.security.JwtUtil;
import com.moviebookingapp.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @MockBean
        private UserRepository userRepository;

        // @MockBean
        // private KafkaProducerService kafkaProducer;

        private ObjectMapper objectMapper;
        private User testUser;

        @BeforeEach
        void setUp() {
                objectMapper = new ObjectMapper();
                testUser = new User(
                                "1", "John", "Doe", "john@example.com",
                                "john123", "password", "password", "1234567890", "USER");
        }

        @Test
        void testRegister_Success() throws Exception {
                Mockito.when(userService.registerUser(any(User.class)))
                                .thenReturn("Registration successful");

                mockMvc.perform(post("/api/v1.0/moviebooking/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testUser)))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Registration successful"));
        }

        @Test
        void testRegister_Failure_MissingField() throws Exception {
                testUser.setEmail(null); // missing email

                Mockito.when(userService.registerUser(any(User.class)))
                                .thenReturn("All fields are mandatory");

                mockMvc.perform(post("/api/v1.0/moviebooking/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testUser)))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("All fields are mandatory"));
        }

        @Test
        void testLogin_Success() throws Exception {
                Mockito.when(userService.loginUser(eq("john123"), eq("password")))
                                .thenReturn("Login successful");

                Mockito.when(userRepository.findByLoginId("john123"))
                                .thenReturn(Optional.of(testUser));

                mockMvc.perform(post("/api/v1.0/moviebooking/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testUser)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("john123"))
                                .andExpect(jsonPath("$.role").value("USER"))
                                .andExpect(jsonPath("$.token").exists())
                                .andExpect(jsonPath("$.message").value("Login successful"));
        }

        @Test
        void testLogin_Failure() throws Exception {
                Mockito.when(userService.loginUser(eq("john123"), eq("wrongpassword")))
                                .thenReturn("Invalid credentials");

                testUser.setPassword("wrongpassword");

                mockMvc.perform(post("/api/v1.0/moviebooking/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testUser)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.message").value("Invalid credentials"));
        }

        // @Test
        // void testForgotPassword_Success() throws Exception {
        //         testUser.setLoginId("john123");
        //         testUser.setPassword("oldPassword"); // <- must be different from new password

        //         Mockito.when(userRepository.findByLoginId("john123"))
        //                         .thenReturn(Optional.of(testUser));

        //         testUser.setPassword("newPassword"); // will be sent in request
        //         testUser.setConfirmPassword("newPassword");

        //         mockMvc.perform(put("/api/v1.0/moviebooking/forgot")
        //                         .contentType(MediaType.APPLICATION_JSON)
        //                         .content(objectMapper.writeValueAsString(testUser)))
        //                         .andExpect(status().isOk())
        //                         .andExpect(content().string("Password updated successfully"));
        // }

        @Test
        void testForgotPassword_UserNotFound() throws Exception {
                Mockito.when(userRepository.findByLoginId("john123"))
                                .thenReturn(Optional.empty());

                mockMvc.perform(put("/api/v1.0/moviebooking/forgot")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testUser)))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("User not found"));
        }

        @Test
        void testLogout() throws Exception {
                mockMvc.perform(post("/api/v1.0/moviebooking/john123/logout"))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Logged out on client side. No server session for JWT."));
        }

        @TestConfiguration
        static class NoKafkaConfig {

                @Bean
                KafkaProducerService kafkaProducerService() {
                        return new KafkaProducerService(null) {
                                @Override
                                public void sendAppLog(String level, String message) {
                                        // no-op: prevents Kafka calls during tests
                                }
                        };
                }
        }
}
