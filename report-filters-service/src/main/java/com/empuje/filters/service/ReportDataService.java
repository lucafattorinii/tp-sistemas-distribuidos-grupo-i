package com.empuje.filters.service;

import com.empuje.filters.dto.DonationRecord;
import com.empuje.filters.dto.ExcelExportRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ReportDataService {

    public List<DonationRecord> getDonationRecords(ExcelExportRequest request) {
        log.info("Getting donation records for Excel export");

        return List.of(
            DonationRecord.builder()
                .id(1L)
                .category("ALIMENTOS")
                .description("Arroz")
                .quantity(50.0)
                .isDeleted(false)
                .isReceived(true)
                .createdBy("admin@empuje.org")
                .organizationId("empuje-org-001")
                .build(),
            DonationRecord.builder()
                .id(2L)
                .category("ROPA")
                .description("Abrigos")
                .quantity(25.0)
                .isDeleted(false)
                .isReceived(true)
                .createdBy("voluntario@empuje.org")
                .organizationId("empuje-org-001")
                .build()
        );
    }
}
