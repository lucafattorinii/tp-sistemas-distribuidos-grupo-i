package com.empuje.reports.service;

import com.empuje.reports.dto.DonationReportInput;
import com.empuje.reports.dto.DonationSummary;
import com.empuje.reports.repository.DonationRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final DonationRecordRepository donationRecordRepository;

    public List<DonationSummary> getDonationReport(DonationReportInput input) {
        log.info("Generating donation report with filters");

        List<Object[]> results = donationRecordRepository.findGroupedByCategoryAndDeleted(
            input.getCategory(),
            input.getStartDate(),
            input.getEndDate(),
            input.getIsDeleted(),
            input.getIsReceived(),
            input.getOrganizationId()
        );

        return results.stream()
            .map(row -> DonationSummary.builder()
                .category((String) row[0])
                .isDeleted((Boolean) row[1])
                .totalQuantity((Double) row[2])
                .build())
            .collect(Collectors.toList());
    }
}
