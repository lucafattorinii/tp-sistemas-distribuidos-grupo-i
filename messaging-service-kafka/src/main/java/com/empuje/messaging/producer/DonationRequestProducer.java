package com.empuje.messaging.producer;

import com.empuje.messaging.model.DonationRequestMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DonationRequestProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "solicitud-donaciones";

    public void publishDonationRequest(DonationRequestMessage message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(TOPIC, message.getOrganizationId(), messageJson)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Solicitud de donación publicada exitosamente en tópico {}: {}",
                                   TOPIC, message.getRequestId());
                        } else {
                            log.error("Error publicando solicitud de donación en tópico {}: {}",
                                    TOPIC, ex.getMessage());
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Error serializando mensaje de solicitud de donación: {}", e.getMessage());
        }
    }
}
