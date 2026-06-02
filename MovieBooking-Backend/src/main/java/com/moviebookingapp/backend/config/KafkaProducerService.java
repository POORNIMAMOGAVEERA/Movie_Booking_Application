package com.moviebookingapp.backend.config;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String,String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String,String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTicketUpdate(String key, String message) {
        kafkaTemplate.send("ticket-updates", key, message);
    }

    public void sendAppLog(String level, String message) {
        // prefix with level for simple processing in Logstash/consumer
        kafkaTemplate.send("app-logs", level, message);
    }
}
