package com.empuje.messaging.consumer;

import com.empuje.messaging.model.DonationOfferMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DonationOfferConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "oferta-donaciones", groupId = "empuje-messaging-group")
    public void consumeDonationOffer(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            DonationOfferMessage offerMessage = objectMapper.readValue(message, DonationOfferMessage.class);

            log.info("Procesando oferta de donación recibida:");
            log.info("Oferta ID: {}", offerMessage.getOfferId());
            log.info("Organización donante: {}", offerMessage.getDonorOrganizationId());
            log.info("Donación: {} - {} ({})",
                   offerMessage.getDonationCategory(),
                   offerMessage.getDonationDescription(),
                   offerMessage.getQuantity());

            // Aquí iría la lógica para:
            // 1. Guardar la oferta en la base de datos
            // 2. Mostrar en interfaz de ofertas disponibles

            acknowledgment.acknowledge();
            log.info("Oferta procesada exitosamente");

        } catch (Exception e) {
            log.error("Error procesando oferta: {}", e.getMessage());
        }
    }
}
