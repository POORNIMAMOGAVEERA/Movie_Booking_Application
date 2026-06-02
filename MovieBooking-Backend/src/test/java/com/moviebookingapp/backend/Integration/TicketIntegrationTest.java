package com.moviebookingapp.backend.Integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebookingapp.backend.model.Movie;
import com.moviebookingapp.backend.model.Ticket;
import com.moviebookingapp.backend.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class TicketIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final RestTemplate client = new RestTemplate();

    @BeforeEach
    void cleanup() {
        mongoTemplate.getDb().drop();
    }

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1.0/moviebooking";
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    @Order(1)
    void testTicketBookingFlow() {

        // ---------------------------
        // REGISTER NORMAL USER
        // ---------------------------
        User user = new User();
        user.setFirstName("Tom");
        user.setLastName("Jerry");
        user.setEmail("u" + System.currentTimeMillis() + "@mail.com");
        user.setLoginId("user_" + System.currentTimeMillis());
        user.setPassword("pass");
        user.setConfirmPassword("pass");
        user.setContactNumber("9999999990");
        user.setRole("USER");

        ResponseEntity<String> regUser = client.postForEntity(
                baseUrl() + "/register",
                user,
                String.class
        );
        assertEquals(200, regUser.getStatusCodeValue(), "User registration failed!");

        // ---------------------------
        // LOGIN NORMAL USER
        // ---------------------------
        ResponseEntity<Map> loginRes = client.postForEntity(
                baseUrl() + "/login",
                Map.of("loginId", user.getLoginId(), "password", "pass"),
                Map.class
        );
        assertEquals(200, loginRes.getStatusCodeValue(), "User login failed!");

        Map<String, Object> loginBody = loginRes.getBody();
        assertNotNull(loginBody, "Login response body is null");
        String userToken = (String) loginBody.get("token");
        assertNotNull(userToken, "JWT token missing for user");

        HttpHeaders userHeaders = jsonHeaders();
        userHeaders.setBearerAuth(userToken); // set Authorization: Bearer <token>

        // ---------------------------
        // REGISTER ADMIN USER
        // ---------------------------
        User admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setEmail("a" + System.currentTimeMillis() + "@mail.com");
        admin.setLoginId("admin_" + System.currentTimeMillis());
        admin.setPassword("adminpass");
        admin.setConfirmPassword("adminpass");
        admin.setContactNumber("8888888888");
        admin.setRole("ADMIN");

        ResponseEntity<String> regAdmin = client.postForEntity(
                baseUrl() + "/register",
                admin,
                String.class
        );
        assertEquals(200, regAdmin.getStatusCodeValue(), "Admin registration failed!");

        // ---------------------------
        // LOGIN ADMIN USER
        // ---------------------------
        ResponseEntity<Map> adminLogin = client.postForEntity(
                baseUrl() + "/login",
                Map.of("loginId", admin.getLoginId(), "password", "adminpass"),
                Map.class
        );
        assertEquals(200, adminLogin.getStatusCodeValue(), "Admin login failed!");
        Map<String, Object> adminBody = adminLogin.getBody();
        assertNotNull(adminBody, "Admin login response is null");
        String adminToken = (String) adminBody.get("token");
        assertNotNull(adminToken, "JWT token missing for admin");

        HttpHeaders adminHeaders = jsonHeaders();
        adminHeaders.setBearerAuth(adminToken);

        // ---------------------------
        // ADD MOVIE (ADMIN ACTION)
        // ---------------------------
        Movie movie = new Movie(null, "AvatarX", "PVR", 10, 10, "Available");
        HttpEntity<Movie> addReq = new HttpEntity<>(movie, adminHeaders);
        ResponseEntity<String> addMovie = client.postForEntity(
                baseUrl() + "/movies/add",
                addReq,
                String.class
        );
        assertEquals(200, addMovie.getStatusCodeValue(), "Movie add failed!");

        // ---------------------------
        // BOOK TICKET (USER)
        // ---------------------------
        Ticket ticket = new Ticket(null, "AvatarX", "PVR", 2, "A1,A2", user.getLoginId());
        HttpEntity<Ticket> ticketReq = new HttpEntity<>(ticket, userHeaders);
        ResponseEntity<String> bookRes = client.exchange(
                baseUrl() + "/AvatarX/add",
                HttpMethod.POST,
                ticketReq,
                String.class
        );
        assertEquals(200, bookRes.getStatusCodeValue(), "Ticket booking failed!");

        // ---------------------------
        // CHECK BOOKED SEATS (ADMIN)
        // ---------------------------
        ResponseEntity<List> seatsRes = client.exchange(
                baseUrl() + "/admin/movies/AvatarX/PVR/booked-seats",
                HttpMethod.GET,
                new HttpEntity<>(adminHeaders),
                List.class
        );
        assertEquals(200, seatsRes.getStatusCodeValue(), "Fetching booked seats failed!");
        assertTrue(seatsRes.getBody().contains("A1"));
        assertTrue(seatsRes.getBody().contains("A2"));

        // ---------------------------
        // REFRESH AVAILABILITY (ADMIN)
        // ---------------------------
        ResponseEntity<String> refreshRes = client.exchange(
                baseUrl() + "/admin/movies/AvatarX/PVR/refresh",
                HttpMethod.PUT,
                new HttpEntity<>(null, adminHeaders),
                String.class
        );
        assertEquals(200, refreshRes.getStatusCodeValue(), "Refreshing availability failed!");
        assertTrue(refreshRes.getBody().contains("Updated"));
    }
}
