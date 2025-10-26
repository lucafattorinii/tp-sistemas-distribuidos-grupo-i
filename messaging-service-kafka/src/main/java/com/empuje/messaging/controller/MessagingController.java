package com.empuje.messaging.controller;

import com.empuje.messaging.model.*;
import com.empuje.messaging.producer.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/messaging")
@RequiredArgsConstructor
public class MessagingController {

    private final EventParticipationProducer eventParticipationProducer;
    private final DonationRequestProducer donationRequestProducer;
    private final DonationTransferProducer donationTransferProducer;
    private final DonationOfferProducer donationOfferProducer;
    private final ExternalEventProducer externalEventProducer;

    @PostMapping("/solicitud-donaciones")
    public ResponseEntity<String> publicarSolicitudDonaciones(
            @RequestParam String organizationId,
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam String quantity) {

        try {
            String requestId = UUID.randomUUID().toString();

            DonationRequestMessage.DonationItem item = DonationRequestMessage.DonationItem.builder()
                    .category(category)
                    .description(description)
                    .quantity(quantity)
                    .build();

            DonationRequestMessage message = DonationRequestMessage.builder()
                    .organizationId(organizationId)
                    .requestId(requestId)
                    .donations(java.util.Arrays.asList(item))
                    .timestamp(LocalDateTime.now())
                    .build();

            donationRequestProducer.publishDonationRequest(message);

            log.info("Solicitud de donación publicada: {}", requestId);
            return ResponseEntity.ok("Solicitud publicada exitosamente con ID: " + requestId);

        } catch (Exception e) {
            log.error("Error publicando solicitud de donación: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error publicando solicitud");
        }
    }

    @PostMapping("/transferencia-donaciones/{targetOrganizationId}")
    public ResponseEntity<String> publicarTransferenciaDonaciones(
            @PathVariable String targetOrganizationId,
            @RequestParam String requestId,
            @RequestParam String donorOrganizationId,
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam String quantity) {

        try {
            DonationTransferMessage message = DonationTransferMessage.builder()
                    .requestId(requestId)
                    .donorOrganizationId(donorOrganizationId)
                    .donationCategory(category)
                    .donationDescription(description)
                    .quantity(quantity)
                    .timestamp(LocalDateTime.now().toString())
                    .build();

            donationTransferProducer.publishDonationTransfer(targetOrganizationId, message);

            log.info("Transferencia de donación publicada para organización: {}", targetOrganizationId);
            return ResponseEntity.ok("Transferencia publicada exitosamente");

        } catch (Exception e) {
            log.error("Error publicando transferencia: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error publicando transferencia");
        }
    }

    @PostMapping("/oferta-donaciones")
    public ResponseEntity<String> publicarOfertaDonaciones(
            @RequestParam String offerId,
            @RequestParam String donorOrganizationId,
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam String quantity) {

        try {
            DonationOfferMessage message = DonationOfferMessage.builder()
                    .offerId(offerId)
                    .donorOrganizationId(donorOrganizationId)
                    .donationCategory(category)
                    .donationDescription(description)
                    .quantity(quantity)
                    .timestamp(LocalDateTime.now().toString())
                    .build();

            donationOfferProducer.publishDonationOffer(message);

            log.info("Oferta de donación publicada: {}", offerId);
            return ResponseEntity.ok("Oferta publicada exitosamente");

        } catch (Exception e) {
            log.error("Error publicando oferta: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error publicando oferta");
        }
    }

    @PostMapping("/eventos-externos")
    public ResponseEntity<String> publicarEventoExterno(
            @RequestParam String organizationId,
            @RequestParam String eventId,
            @RequestParam String eventName,
            @RequestParam String description,
            @RequestParam String eventDateTime) {

        try {
            ExternalEventMessage message = ExternalEventMessage.builder()
                    .organizationId(organizationId)
                    .eventId(eventId)
                    .eventName(eventName)
                    .description(description)
                    .eventDateTime(eventDateTime)
                    .timestamp(LocalDateTime.now().toString())
                    .build();

            externalEventProducer.publishExternalEvent(message);

            log.info("Evento externo publicado: {}", eventId);
            return ResponseEntity.ok("Evento externo publicado exitosamente");

        } catch (Exception e) {
            log.error("Error publicando evento externo: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error publicando evento externo");
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Servicio de mensajería Kafka funcionando correctamente");
    }
}
