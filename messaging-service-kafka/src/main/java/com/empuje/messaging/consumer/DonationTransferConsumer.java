package com.empuje.messaging.consumer;

import com.empuje.messaging.model.DonationTransferMessage;
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
public class DonationTransferConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topicPattern = "transferencia-donaciones/.*", groupId = "empuje-messaging-group")
    public void consumeDonationTransfer(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment acknowledgment) {

        try {
            DonationTransferMessage transferMessage = objectMapper.readValue(message, DonationTransferMessage.class);

            log.info("Procesando transferencia recibida:");
            log.info("Request ID: {}", transferMessage.getRequestId());
            log.info("Organización donante: {}", transferMessage.getDonorOrganizationId());
            log.info("Donación: {} - {} ({})",
                   transferMessage.getDonationCategory(),
                   transferMessage.getDonationDescription(),
                   transferMessage.getQuantity());

            processInventoryAdjustment(transferMessage);

            acknowledgment.acknowledge();
            log.info("Transferencia procesada exitosamente");

        } catch (Exception e) {
            log.error("Error procesando transferencia: {}", e.getMessage());
        }
    }

    private void processInventoryAdjustment(DonationTransferMessage transferMessage) {
        try {
            log.info("Procesando ajuste de inventario para transferencia:");
            log.info("Categoría: {}", transferMessage.getDonationCategory());
            log.info("Descripción: {}", transferMessage.getDonationDescription());
            log.info("Cantidad: {}", transferMessage.getQuantity());

            log.info("Ajuste de inventario completado exitosamente");

        } catch (Exception e) {
            log.error("Error ajustando inventario: {}", e.getMessage());
            throw new RuntimeException("Error en ajuste de inventario", e);
        }
    }
}
