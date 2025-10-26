package com.empuje.soap.controller;

import com.empuje.soap.dto.OrganizationsResponse;
import com.empuje.soap.dto.PresidentsResponse;
import com.empuje.soap.dto.SoapRequest;
import com.empuje.soap.service.SoapClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/soap")
@RequiredArgsConstructor
@Tag(name = "SOAP Client", description = "API para consultar presidentes y ONGs mediante SOAP")
public class SoapController {

    private final SoapClientService soapClientService;

    @PostMapping("/presidents")
    @Operation(summary = "Consultar presidentes por IDs de organizaciones",
               description = "Consulta los datos de los presidentes de las organizaciones especificadas usando SOAP list_presidents")
    public ResponseEntity<PresidentsResponse> getPresidents(
            @RequestBody SoapRequest request,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        log.info("SOAP Request - Presidents for user: {}, organizations: {}", userId, request.getOrganizationIds());

        try {
            PresidentsResponse response = soapClientService.getPresidentsByOrganizationIds(request.getOrganizationIds());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in SOAP presidents request: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/organizations")
    @Operation(summary = "Consultar ONGs por IDs",
               description = "Consulta los datos completos de las organizaciones especificadas usando SOAP list_associations")
    public ResponseEntity<OrganizationsResponse> getOrganizations(
            @RequestBody SoapRequest request,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        log.info("SOAP Request - Organizations for user: {}, organizations: {}", userId, request.getOrganizationIds());

        try {
            OrganizationsResponse response = soapClientService.getOrganizationsByIds(request.getOrganizationIds());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in SOAP organizations request: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Verificar estado del servicio SOAP",
               description = "Verifica que el cliente SOAP esté funcionando correctamente")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("SOAP Client Service funcionando correctamente\n" +
                               "WSDL: " + "https://soap-app-latest.onrender.com/?wsdl");
    }

    private Long getUserIdFromAuth(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("Authentication required");
        }
        return Long.valueOf(auth.getPrincipal().toString());
    }
}
