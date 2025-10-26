package com.empuje.reports.controller;

import com.empuje.reports.dto.*;
import com.empuje.reports.model.CustomFilter;
import com.empuje.reports.service.CustomFilterService;
import com.empuje.reports.service.EventParticipationService;
import com.empuje.reports.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ReportGraphQLController {

    private final ReportService reportService;
    private final CustomFilterService customFilterService;
    private final EventParticipationService eventParticipationService;

    @QueryMapping
    public List<DonationSummary> donationReport(@Argument DonationReportInput input) {
        log.info("GraphQL Query: donationReport with filters");
        return reportService.getDonationReport(input);
    }

    @QueryMapping
    public List<EventParticipationSummary> eventParticipationReport(@Argument EventParticipationInput input) {
        log.info("GraphQL Query: eventParticipationReport with filters");
        return eventParticipationService.getEventParticipationReport(input);
    }

    @QueryMapping
    public List<CustomFilter> customFilters(@Argument Long userId, @Argument String filterType) {
        log.info("GraphQL Query: customFilters for user: {} and type: {}", userId, filterType);
        return customFilterService.getUserFilters(userId, filterType);
    }

    @MutationMapping
    public CustomFilter saveCustomFilter(@Argument CustomFilterInput input, @Argument Long userId) {
        log.info("GraphQL Mutation: saveCustomFilter for user: {}", userId);

        CustomFilter filter = CustomFilter.builder()
                .name(input.getName())
                .filterType(input.getFilterType())
                .userId(userId)
                .category(input.getCategory())
                .startDate(input.getStartDate())
                .endDate(input.getEndDate())
                .isDeleted(input.getIsDeleted())
                .hasDonationDistribution(input.getHasDonationDistribution())
                .build();

        return customFilterService.saveFilter(filter);
    }

    @MutationMapping
    public CustomFilter updateCustomFilter(@Argument Long filterId, @Argument CustomFilterInput input, @Argument Long userId) {
        log.info("GraphQL Mutation: updateCustomFilter: {} for user: {}", filterId, userId);

        CustomFilter filter = CustomFilter.builder()
                .name(input.getName())
                .category(input.getCategory())
                .startDate(input.getStartDate())
                .endDate(input.getEndDate())
                .isDeleted(input.getIsDeleted())
                .hasDonationDistribution(input.getHasDonationDistribution())
                .build();

        return customFilterService.updateFilter(filterId, filter, userId);
    }

    @MutationMapping
    public Boolean deleteCustomFilter(@Argument Long filterId, @Argument Long userId) {
        log.info("GraphQL Mutation: deleteCustomFilter: {} for user: {}", filterId, userId);
        customFilterService.deleteFilter(filterId, userId);
        return true;
    }
}
