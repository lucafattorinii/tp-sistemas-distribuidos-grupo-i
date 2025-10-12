package com.empuje.messaging.producer;

import com.empuje.messaging.model.EventCancellationMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventCancellationProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "baja-evento-solidario";

    public void publishEventCancellation(EventCancellationMessage message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(TOPIC, message.getOrganizationId(), messageJson)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Baja de evento publicada en tópico {}: {}",
                                   TOPIC, message.getEventId());
                        } else {
                            log.error("Error publicando baja de evento en tópico {}: {}",
                                    TOPIC, ex.getMessage());
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Error serializando mensaje de baja de evento: {}", e.getMessage());
        }
    }
}
