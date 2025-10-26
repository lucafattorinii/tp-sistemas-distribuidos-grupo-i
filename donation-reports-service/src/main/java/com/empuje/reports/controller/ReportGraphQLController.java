package com.empuje.reports.controller;

import com.empuje.reports.dto.DonationReportInput;
import com.empuje.reports.dto.DonationSummary;
import com.empuje.reports.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ReportGraphQLController {

    private final ReportService reportService;

    @QueryMapping
    public List<DonationSummary> donationReport(@Argument DonationReportInput input) {
        log.info("GraphQL Query: donationReport with filters");
        return reportService.getDonationReport(input);
    }
}
