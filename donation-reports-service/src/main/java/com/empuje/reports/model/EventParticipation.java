package com.empuje.reports.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_participation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "event_description")
    private String eventDescription;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "has_donation_distribution")
    @Builder.Default
    private Boolean hasDonationDistribution = false;

    @Column(name = "participation_date", nullable = false)
    private LocalDateTime participationDate;

    @Column(name = "organization_id")
    private String organizationId;
}
