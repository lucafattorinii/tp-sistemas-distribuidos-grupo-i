package com.empuje.messaging.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalEventMessage {
    private String organizationId;
    private String eventId;
    private String eventName;
    private String description;
    private String eventDateTime;
    private String timestamp;
}
