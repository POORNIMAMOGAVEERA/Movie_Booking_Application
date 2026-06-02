package com.moviebookingapp.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI movieBookingAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Movie Booking App API")
                        .description("Backend API documentation for Movie Booking System")
                        .version("v1.0")
                );
    }
}
