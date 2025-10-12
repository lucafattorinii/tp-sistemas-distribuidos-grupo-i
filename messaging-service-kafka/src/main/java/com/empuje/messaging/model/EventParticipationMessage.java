package com.empuje.messaging.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipationMessage {
    private String eventId;
    private String volunteerOrganizationId;
    private String volunteerId;
    private String volunteerName;
    private String volunteerLastName;
    private String volunteerPhone;
    private String volunteerEmail;
    private String timestamp;
}
