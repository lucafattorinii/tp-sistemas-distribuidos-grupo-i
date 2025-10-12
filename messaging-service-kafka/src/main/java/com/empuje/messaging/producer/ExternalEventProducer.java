package com.empuje.messaging.producer;

import com.empuje.messaging.model.ExternalEventMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "eventos-solidarios";

    public void publishExternalEvent(ExternalEventMessage message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(TOPIC, message.getOrganizationId(), messageJson)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Evento externo publicado en tópico {}: {}",
                                   TOPIC, message.getEventId());
                        } else {
                            log.error("Error publicando evento externo en tópico {}: {}",
                                    TOPIC, ex.getMessage());
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Error serializando mensaje de evento externo: {}", e.getMessage());
        }
    }
}
