package com.empuje.messaging.producer;

import com.empuje.messaging.model.RequestCancellationMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestCancellationProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "baja-solicitud-donaciones";

    public void publishRequestCancellation(RequestCancellationMessage message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(TOPIC, message.getOrganizationId(), messageJson)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Baja de solicitud publicada en tópico {}: {}",
                                   TOPIC, message.getRequestId());
                        } else {
                            log.error("Error publicando baja de solicitud en tópico {}: {}",
                                    TOPIC, ex.getMessage());
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Error serializando mensaje de baja de solicitud: {}", e.getMessage());
        }
    }
}
