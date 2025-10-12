package com.empuje.messaging.producer;

import com.empuje.messaging.model.DonationTransferMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DonationTransferProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishDonationTransfer(String targetOrganizationId, DonationTransferMessage message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            String topic = "transferencia-donaciones/" + targetOrganizationId;

            kafkaTemplate.send(topic, message.getRequestId(), messageJson)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Transferencia de donación publicada en tópico {}: {}",
                                   topic, message.getRequestId());
                        } else {
                            log.error("Error publicando transferencia en tópico {}: {}",
                                    topic, ex.getMessage());
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Error serializando mensaje de transferencia: {}", e.getMessage());
        }
    }
}
