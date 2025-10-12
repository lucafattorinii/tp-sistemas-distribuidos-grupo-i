package com.empuje.messaging.producer;

import com.empuje.messaging.model.EventParticipationMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventParticipationProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishEventParticipation(String organizerId, EventParticipationMessage message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            String topic = "adhesion-evento/" + organizerId;

            kafkaTemplate.send(topic, message.getVolunteerId(), messageJson)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Adhesión a evento publicada en tópico {}: {}",
                                   topic, message.getEventId());
                        } else {
                            log.error("Error publicando adhesión en tópico {}: {}",
                                    topic, ex.getMessage());
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Error serializando mensaje de adhesión: {}", e.getMessage());
        }
    }
}
