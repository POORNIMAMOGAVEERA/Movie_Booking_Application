package com.moviebookingapp.backend.Integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebookingapp.backend.config.KafkaProducerService;
import com.moviebookingapp.backend.model.User;
import com.moviebookingapp.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = com.moviebookingapp.backend.MovieBookingApplication.class
)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.mongodb.embedded.version=6.0.0"
})
public class UserControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private final RestTemplate client = new RestTemplate();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper mapper;

    // Provide dummy KafkaProducerService to prevent 500
    @TestConfiguration
    static class DummyKafkaConfig {
        @Bean
        public KafkaProducerService kafkaProducerService() {
            return new KafkaProducerService(null) {
                @Override
                public void sendAppLog(String level, String message) {
                    // no-op for integration test
                }
            };
        }
    }

    @BeforeEach
    void cleanDb() {
        userRepository.deleteAll();
    }

    @Test
    void testUserRegistrationAndLoginFlow() {
        String url = "http://localhost:" + port + "/api/v1.0/moviebooking";

        User u = new User();
        u.setFirstName("Poo");
        u.setLastName("Mog");
        u.setEmail("p@gmail.com");
        u.setLoginId("poo123");
        u.setPassword("12345");
        u.setConfirmPassword("12345");
        u.setContactNumber("9999999999");
        u.setRole("USER");

        // REGISTER
        ResponseEntity<String> reg = client.postForEntity(url + "/register", u, String.class);
        assertEquals(200, reg.getStatusCodeValue());

        // LOGIN
        Map<String, String> loginReq = Map.of(
                "loginId", "poo123",
                "password", "12345"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> req = new HttpEntity<>(loginReq, headers);

        ResponseEntity<Map> loginRes = client.exchange(
                url + "/login",
                HttpMethod.POST,
                req,
                Map.class
        );

        assertEquals(200, loginRes.getStatusCodeValue());
        assertNotNull(loginRes.getBody());
        assertEquals("poo123", loginRes.getBody().get("username"));
        assertTrue(loginRes.getBody().containsKey("token"));
    }
}
