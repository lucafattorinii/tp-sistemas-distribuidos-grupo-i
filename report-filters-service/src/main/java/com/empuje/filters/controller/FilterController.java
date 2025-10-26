package com.empuje.filters.controller;

import com.empuje.filters.dto.ExcelExportRequest;
import com.empuje.filters.service.ExcelExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/filters")
@RequiredArgsConstructor
@Tag(name = "Filtros y Exportación", description = "API para filtros y exportación Excel")
public class FilterController {

    private final ExcelExportService excelExportService;

    @PostMapping("/export/excel")
    @Operation(summary = "Exportar informe a Excel",
               description = "Genera y descarga un informe de donaciones en formato Excel")
    public ResponseEntity<ByteArrayResource> exportToExcel(
            @RequestBody ExcelExportRequest request) {

        log.info("Exporting to Excel");

        try {
            byte[] excelData = excelExportService.generateExcelReport(request);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "donation_report.xlsx");
            headers.setContentLength(excelData.length);

            ByteArrayResource resource = new ByteArrayResource(excelData);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (Exception e) {
            log.error("Error exporting to Excel: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
