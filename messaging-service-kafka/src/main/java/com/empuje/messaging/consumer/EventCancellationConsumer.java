package com.empuje.messaging.consumer;

import com.empuje.messaging.model.EventCancellationMessage;
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
public class EventCancellationConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "baja-evento-solidario", groupId = "empuje-messaging-group")
    public void consumeEventCancellation(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            EventCancellationMessage cancellationMessage = objectMapper.readValue(message, EventCancellationMessage.class);

            log.info("Procesando baja de evento:");
            log.info("Organización: {}", cancellationMessage.getOrganizationId());
            log.info("Evento ID: {}", cancellationMessage.getEventId());

            // Aquí iría la lógica para:
            // 1. Marcar el evento como cancelado en nuestra base de datos
            // 2. Remover el evento de la lista de eventos externos disponibles
            // 3. Cancelar cualquier adhesión pendiente relacionada

            acknowledgment.acknowledge();
            log.info("Baja de evento procesada exitosamente");

        } catch (Exception e) {
            log.error("Error procesando baja de evento: {}", e.getMessage());
        }
    }
}
