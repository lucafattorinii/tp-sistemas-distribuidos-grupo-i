package com.empuje.eventservice.model;

import com.empuje.eventservice.validation.FutureDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(max = 200, message = "El nombre del evento no puede exceder los 200 caracteres")
    @Column(nullable = false, length = 200)
    private String name;

    @Size(max = 2000, message = "La descripción no puede exceder los 2000 caracteres")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "La fecha y hora del evento son obligatorias")
    @FutureDateTime
    @Column(name = "event_datetime", nullable = false)
    private Instant eventDatetime;

    @NotNull(message = "El estado activo no puede ser nulo")
    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
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
        this.updatedAt = this.createdAt;
        if (this.eventDatetime != null) {
            this.eventDatetime = this.eventDatetime.atOffset(ZoneOffset.UTC).toInstant();
        }
        // Ensure active is never null
        if (this.active == null) {
            this.active = true;
        }
    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt = Instant.now();
    }
}
