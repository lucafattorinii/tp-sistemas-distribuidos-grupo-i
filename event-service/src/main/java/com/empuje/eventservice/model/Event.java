package com.empuje.eventservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "event_datetime", nullable = false)
    private Instant eventDatetime;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<EventParticipant> participants = new HashSet<>();

    @PrePersist
    public void onCreate(){
        this.createdAt = Instant.now();
        if (this.eventDatetime != null) {
            this.eventDatetime = this.eventDatetime.atOffset(ZoneOffset.UTC).toInstant();
        }
    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt = Instant.now();
    }
}
