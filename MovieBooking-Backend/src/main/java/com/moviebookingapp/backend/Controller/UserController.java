package com.moviebookingapp.backend.Controller;

import com.moviebookingapp.backend.config.KafkaProducerService;
import com.moviebookingapp.backend.model.User;
import com.moviebookingapp.backend.repository.UserRepository;
import com.moviebookingapp.backend.security.JwtUtil;
import com.moviebookingapp.backend.security.TokenHelper;
import com.moviebookingapp.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1.0/moviebooking")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducer;

    @Autowired
    public UserController(UserService userService, KafkaProducerService kafkaProducer, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.kafkaProducer = kafkaProducer;
    }

    // Register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        String result = userService.registerUser(user);
        // Log to Kafka
        kafkaProducer.sendAppLog("INFO",
        String.format("REGISTER | %s | loginId=%s | result=%s",
        Instant.now().toString(), user.getLoginId(), result));
        if (result.equals("Registration successful")) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.badRequest().body(result);
    }

    // Login -> sets session attributes
    // @PostMapping("/login")
    // public ResponseEntity<?> login(@RequestBody User request, HttpSession
    // session) {
    // String result = userService.loginUser(request.getLoginId(),
    // request.getPassword());

    // if (result.equals("Login successful")) {
    // Optional<User> userOpt = userRepository.findByLoginId(request.getLoginId());
    // if (userOpt.isPresent()) {
    // User u = userOpt.get();

    // // Store in session
    // session.setAttribute("loggedInUser", u.getLoginId());
    // session.setAttribute("role", u.getRole());

    // // Log to Kafka
    // // kafkaProducer.sendAppLog("INFO",
    // // String.format("LOGIN SUCCESS | %s | loginId=%s",
    // // Instant.now().toString(), request.getLoginId()));

    // // Return structured JSON
    // Map<String, Object> response = new HashMap<>();
    // response.put("username", u.getLoginId());
    // response.put("role", u.getRole());
    // response.put("message", "Login successful");

    // return ResponseEntity.ok(response);
    // }
    // }

    // // Invalid login
    // // kafkaProducer.sendAppLog("WARN",
    // // String.format("LOGIN FAILURE | %s | loginId=%s | reason=%s",
    // // Instant.now().toString(), request.getLoginId(), result));

    // return ResponseEntity.status(401).body(Map.of("message", "Invalid
    // credentials"));
    // }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User request) {

        String result = userService.loginUser(request.getLoginId(), request.getPassword());

        if (!result.equals("Login successful")) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }

        User user = userRepository.findByLoginId(request.getLoginId()).get();

        // generate token
        String token = JwtUtil.generateToken(user.getLoginId(), user.getRole());

        return ResponseEntity.ok(
                Map.of(
                        "username", user.getLoginId(),
                        "role", user.getRole(),
                        "token", token,
                        "message", "Login successful"));
    }

    // Forgot / reset password (allowed only if logged-in user matches or admin)
    @PutMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody User request) {

        String loginId = request.getLoginId();
        String newPassword = request.getPassword();

        Optional<User> userOpt = userRepository.findByLoginId(loginId);

        // --- USER NOT FOUND ---
        if (userOpt.isEmpty()) {
            kafkaProducer.sendAppLog("WARN",
            String.format("FORGOT FAILED | %s | loginId=%s | reason=User Not Found",
            Instant.now().toString(), loginId));

            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOpt.get();

        // --- EMAIL DOES NOT MATCH ---
        if (!user.getLoginId().equals(loginId)) {

            kafkaProducer.sendAppLog("WARN",
            String.format("FORGOT FAILED | %s | loginId=%s | reason=Login Id Mismatch",
            Instant.now().toString(), loginId));

            return ResponseEntity.badRequest().body("Login Id does not match!");
        }

        if (user.getPassword().equals(request.getPassword())) {
            kafkaProducer.sendAppLog("WARN",
            String.format("RESET FAILED | %s | username=%s | reason=OLD_PASSWORD_MATCH",
            Instant.now().toString(), user.getLoginId()));

            return ResponseEntity.badRequest().body("New password cannot be the same as the old password.");
        }

        // --- UPDATE PASSWORD ---
        user.setPassword(newPassword);
        user.setConfirmPassword(newPassword);
        userRepository.save(user);

        kafkaProducer.sendAppLog("INFO",
        String.format("FORGOT SUCCESS | %s | loginId=%s",
        Instant.now().toString(), loginId));

        return ResponseEntity.ok("Password updated successfully");
    }

    // // Logout
    // @PostMapping("/{username}/logout")
    // public ResponseEntity<?> logout(@PathVariable String username, HttpSession
    // session) {
    // String current = (String) session.getAttribute("loggedInUser");
    // if (current == null || !current.equals(username)) {
    // // allow logout even if they are not the one — simply invalidate if they are
    // // logged in
    // session.invalidate();
    // return ResponseEntity.ok("Session invalidated");
    // }
    // session.invalidate();
    // // kafkaProducer.sendAppLog("INFO",
    // // String.format("LOGOUT | %s | loginId=%s | performedBy=%s",
    // // Instant.now().toString(), username, current == null ? "unknown" :
    // current));
    // return ResponseEntity.ok("Logged out successfully");
    // }

    @PostMapping("/{username}/logout")
    public ResponseEntity<?> logout(@PathVariable String username) {
        return ResponseEntity.ok("Logged out on client side. No server session for JWT.");
    }

    // Quick check session
    // @GetMapping("/session")
    // public ResponseEntity<?> checkSession(HttpSession session) {
    // String user = (String) session.getAttribute("loggedInUser");
    // if (user == null)
    // return ResponseEntity.status(401).body("No active session");
    // return ResponseEntity.ok("Active session for " + user);
    // }

    // @PostMapping("/{username}/forgot")
    // public ResponseEntity<?> resetPasswordAfterLogin(
    // @PathVariable String username,
    // @RequestBody User request,
    // HttpSession session) {

    // String requester = (String) session.getAttribute("loggedInUser");
    // String role = (String) session.getAttribute("role");
    // User user = userRepository.findByLoginId(username)
    // .orElse(null);

    // // User must be logged in
    // if (requester == null) {
    // // kafkaProducer.sendAppLog("WARN",
    // // String.format("RESET-PASSWORD | %s | target=%s |
    // result=NOT_AUTHENTICATED",
    // // Instant.now(), username));
    // return ResponseEntity.status(401).body("Please login to reset password");
    // }
    // if (user.getPassword().equals(request.getPassword())) {
    // // kafkaProducer.sendAppLog("WARN",
    // // String.format("RESET FAILED | %s | username=%s |
    // reason=OLD_PASSWORD_MATCH",
    // // Instant.now().toString(), username));

    // return ResponseEntity.badRequest().body("New password cannot be the same as
    // the old password.");
    // }

    // String result = userService.forgotPassword(
    // username,
    // request.getPassword(),
    // requester,
    // role);

    // // Kafka logging
    // // kafkaProducer.sendAppLog(
    // // result.toLowerCase().contains("success") ? "INFO" : "WARN",
    // // String.format("RESET-PASSWORD | %s | user=%s | by=%s | result=%s",
    // // Instant.now(), username, requester, result));

    // if (result.contains("success"))
    // return ResponseEntity.ok("Password updated successfully");

    // return ResponseEntity.badRequest().body(result);
    // }

    @PostMapping("/{username}/forgot")
    public ResponseEntity<?> resetPasswordAfterLogin(
            @PathVariable String username,
            @RequestBody User request,
        @RequestHeader(value = "Authorization", required = false) String authHeader
) {

        String requester = TokenHelper.getUserFromToken(authHeader);
        String role = TokenHelper.getRoleFromToken(authHeader);
        User user = userRepository.findByLoginId(username)
                .orElse(null);

        // User must be logged in
        if (requester == null) {
            kafkaProducer.sendAppLog("WARN",
            String.format("RESET-PASSWORD | %s | target=%s | result=NOT_AUTHENTICATED",
            Instant.now(), username));
            return ResponseEntity.status(401).body("Please login to reset password");
        }
        if (user.getPassword().equals(request.getPassword())) {
            kafkaProducer.sendAppLog("WARN",
            String.format("RESET FAILED | %s | username=%s | reason=OLD_PASSWORD_MATCH",
            Instant.now().toString(), username));

            return ResponseEntity.badRequest().body("New password cannot be the same as the old password.");
        }

        String result = userService.forgotPassword(
                username,
                request.getPassword(),
                requester,
                role);

        // Kafka logging
        kafkaProducer.sendAppLog(
        result.toLowerCase().contains("success") ? "INFO" : "WARN",
        String.format("RESET-PASSWORD | %s | user=%s | by=%s | result=%s",
        Instant.now(), username, requester, result));

        if (result.contains("success"))
            return ResponseEntity.ok("Password updated successfully");

        return ResponseEntity.badRequest().body(result);
    }
}
