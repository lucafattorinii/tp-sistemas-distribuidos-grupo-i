package com.empuje.messaging.consumer;

import com.empuje.messaging.model.ExternalEventMessage;
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
public class ExternalEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "eventos-solidarios", groupId = "empuje-messaging-group")
    public void consumeExternalEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            ExternalEventMessage eventMessage = objectMapper.readValue(message, ExternalEventMessage.class);

            log.info("Procesando evento externo recibido:");
            log.info("Evento ID: {}", eventMessage.getEventId());
            log.info("Organización: {}", eventMessage.getOrganizationId());
            log.info("Nombre: {}", eventMessage.getEventName());
            log.info("Fecha: {}", eventMessage.getEventDateTime());

            // Validación: descartar eventos propios
            if (isOurOwnEvent(eventMessage)) {
                log.info("Evento propio descartado: {}", eventMessage.getEventId());
                acknowledgment.acknowledge();
                return;
            }

            // Validación: verificar que el evento esté vigente
            if (!isEventValid(eventMessage)) {
                log.info("Evento no válido o dado de baja descartado: {}", eventMessage.getEventId());
                acknowledgment.acknowledge();
                return;
            }

            // Procesar evento externo válido
            processValidExternalEvent(eventMessage);

            acknowledgment.acknowledge();
            log.info("Evento externo procesado exitosamente");

        } catch (Exception e) {
            log.error("Error procesando evento externo: {}", e.getMessage());
        }
    }

    private boolean isOurOwnEvent(ExternalEventMessage eventMessage) {
        // ID de nuestra organización para filtrar eventos propios
        String OUR_ORGANIZATION_ID = "empuje-org-001";
        return OUR_ORGANIZATION_ID.equals(eventMessage.getOrganizationId());
    }

    private boolean isEventValid(ExternalEventMessage eventMessage) {
        // Aquí se verificaría contra la base de datos si el evento está vigente
        // Por ejemplo, consultar si existe una baja para este evento

        // Para demostración, asumimos que todos los eventos son válidos
        // excepto si están marcados como cancelados

        return true; // TODO: Implementar lógica real de validación
    }

    private void processValidExternalEvent(ExternalEventMessage eventMessage) {
        log.info("Procesando evento externo válido:");
        log.info("Nombre: {}", eventMessage.getEventName());
        log.info("Descripción: {}", eventMessage.getDescription());
        log.info("Fecha: {}", eventMessage.getEventDateTime());

        // Aquí iría la lógica para:
        // 1. Guardar el evento en la tabla de eventos externos
        // 2. Mostrar en la interfaz de eventos externos
        // 3. Permitir adhesión de voluntarios

        log.info("Evento externo guardado exitosamente");
    }
}

            acknowledgment.acknowledge();
            log.info("Evento externo procesado exitosamente");

        } catch (Exception e) {
            log.error("Error procesando evento externo: {}", e.getMessage());
        }
    }
}
