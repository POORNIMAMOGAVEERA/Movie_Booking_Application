package com.moviebookingapp.backend.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic ticketUpdatesTopic() {
        return TopicBuilder.name("ticket-updates").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic appLogsTopic() {
        return TopicBuilder.name("app-logs").partitions(1).replicas(1).build();
    }
}
