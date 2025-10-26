package com.empuje.filters.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomFilterRequest {
    private String name;
    private String filterType;
    private String category;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isDeleted;
    private Boolean hasDonationDistribution;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomFilterResponse {
    private Long id;
    private String name;
    private String filterType;
    private String category;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isDeleted;
    private Boolean hasDonationDistribution;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationRecord {
    private Long id;
    private String category;
    private String description;
    private Double quantity;
    private Boolean isDeleted;
    private Boolean isReceived;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime modifiedDate;
    private String modifiedBy;
    private String organizationId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelExportRequest {
    private String reportType;
    private String category;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isDeleted;
    private Boolean isReceived;
    private Long userId;
    private Boolean hasDonationDistribution;
}
