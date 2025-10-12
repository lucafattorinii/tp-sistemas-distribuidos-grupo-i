package com.empuje.messaging.consumer;

import com.empuje.messaging.model.RequestCancellationMessage;
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
public class RequestCancellationConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "baja-solicitud-donaciones", groupId = "empuje-messaging-group")
    public void consumeRequestCancellation(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            RequestCancellationMessage cancellationMessage = objectMapper.readValue(message, RequestCancellationMessage.class);

            log.info("Procesando baja de solicitud:");
            log.info("Organización: {}", cancellationMessage.getOrganizationId());
            log.info("Solicitud ID: {}", cancellationMessage.getRequestId());

            // Aquí iría la lógica para:
            // 1. Marcar la solicitud como cancelada en nuestra base de datos
            // 2. Invalidar cualquier transferencia pendiente relacionada
            // 3. Actualizar el estado de sincronización

            acknowledgment.acknowledge();
            log.info("Baja de solicitud procesada exitosamente");

        } catch (Exception e) {
            log.error("Error procesando baja de solicitud: {}", e.getMessage());
        }
    }
}
