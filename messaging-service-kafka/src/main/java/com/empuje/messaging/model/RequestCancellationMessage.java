package com.empuje.messaging.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestCancellationMessage {
    private String organizationId;
    private String requestId;
    private String timestamp;
}
