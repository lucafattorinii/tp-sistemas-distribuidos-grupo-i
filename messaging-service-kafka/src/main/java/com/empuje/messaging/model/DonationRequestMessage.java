package com.empuje.messaging.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationRequestMessage {
    @JsonProperty("organizationId")
    private String organizationId;

    @JsonProperty("requestId")
    private String requestId;

    @JsonProperty("donations")
    private List<DonationItem> donations;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationItem {
    @JsonProperty("category")
    private String category;

    @JsonProperty("description")
    private String description;

    @JsonProperty("quantity")
    private String quantity;
}
