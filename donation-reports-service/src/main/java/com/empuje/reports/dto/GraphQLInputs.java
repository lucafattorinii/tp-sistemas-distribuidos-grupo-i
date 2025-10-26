package com.empuje.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationReportInput {
    private String category;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isDeleted;
    private Boolean isReceived;
    private String organizationId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationSummary {
    private String category;
    private Boolean isDeleted;
    private Double totalQuantity;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipationInput {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long userId;
    private Boolean hasDonationDistribution;
    private String organizationId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipationSummary {
    private Integer year;
    private Integer month;
    private LocalDateTime eventDate;
    private String eventName;
    private String eventDescription;
    private Boolean hasDonationDistribution;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomFilterInput {
    private String name;
    private String filterType;
    private String category;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isDeleted;
    private Boolean hasDonationDistribution;
}
