package com.empuje.messaging.producer;

import com.empuje.messaging.model.DonationOfferMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DonationOfferProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "oferta-donaciones";

    public void publishDonationOffer(DonationOfferMessage message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(TOPIC, message.getDonorOrganizationId(), messageJson)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Oferta de donación publicada en tópico {}: {}",
                                   TOPIC, message.getOfferId());
                        } else {
                            log.error("Error publicando oferta en tópico {}: {}",
                                    TOPIC, ex.getMessage());
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Error serializando mensaje de oferta: {}", e.getMessage());
        }
    }
}
