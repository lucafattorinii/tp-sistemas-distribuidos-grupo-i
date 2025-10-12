package com.empuje.messaging.consumer;

import com.empuje.messaging.model.DonationRequestMessage;
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
public class DonationRequestConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "solicitud-donaciones", groupId = "empuje-messaging-group")
    public void consumeDonationRequest(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            DonationRequestMessage requestMessage = objectMapper.readValue(message, DonationRequestMessage.class);

            log.info("Procesando solicitud de donación recibida:");
            log.info("Organización solicitante: {}", requestMessage.getOrganizationId());
            log.info("ID de solicitud: {}", requestMessage.getRequestId());
            log.info("Número de items: {}", requestMessage.getDonations().size());

            // Aquí iría la lógica para:
            // 1. Verificar si la solicitud no está dada de baja
            // 2. Guardar la solicitud en la base de datos
            // 3. Cotejar con inventario disponible

            acknowledgment.acknowledge();
            log.info("Solicitud de donación procesada exitosamente");

        } catch (Exception e) {
            log.error("Error procesando solicitud de donación: {}", e.getMessage());
            // En producción, podrías implementar reintento o manejo de errores más sofisticado
        }
    }
}
