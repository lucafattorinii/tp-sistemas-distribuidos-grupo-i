package com.empuje.messaging.consumer;

import com.empuje.messaging.model.EventParticipationMessage;
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
public class EventParticipationConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topicPattern = "adhesion-evento/.*", groupId = "empuje-messaging-group")
    public void consumeEventParticipation(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment acknowledgment) {

        try {
            EventParticipationMessage participationMessage = objectMapper.readValue(message, EventParticipationMessage.class);

            log.info("Procesando adhesión recibida:");
            log.info("Evento ID: {}", participationMessage.getEventId());
            log.info("Voluntario: {} {} ({})",
                   participationMessage.getVolunteerName(),
                   participationMessage.getVolunteerLastName(),
                   participationMessage.getVolunteerEmail());
            log.info("Organización voluntario: {}", participationMessage.getVolunteerOrganizationId());

            // Aquí iría la lógica para:
            // 1. Verificar que el evento existe y está activo
            // 2. Agregar el voluntario a la lista de participantes
            // 3. Enviar confirmación si es necesario

            acknowledgment.acknowledge();
            log.info("Adhesión procesada exitosamente");

        } catch (Exception e) {
            log.error("Error procesando adhesión: {}", e.getMessage());
        }
    }
}
