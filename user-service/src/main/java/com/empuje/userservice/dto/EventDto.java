package com.empuje.userservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link com.empuje.userservice.model.Event} entity.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EventDto extends BaseDto {
    @NotBlank(message = "El nombre del evento es requerido")
    @Size(max = 200, message = "El nombre del evento no puede exceder los 200 caracteres")
    private String name;

    @Size(max = 1000, message = "La descripción no puede exceder los 1000 caracteres")
    private String description;

    @NotNull(message = "La fecha y hora de inicio son requeridas")
    @FutureOrPresent(message = "La fecha y hora de inicio deben ser en el futuro")
    private LocalDateTime startDatetime;

    @Future(message = "La fecha y hora de finalización deben ser en el futuro")
    private LocalDateTime endDatetime;

    @Size(max = 500, message = "La ubicación no puede exceder los 500 caracteres")
    private String location;

    @Min(value = 1, message = "El número máximo de participantes debe ser al menos 1")
    private Integer maxParticipants;

    @NotNull(message = "El estado del evento es requerido")
    private EventStatusDto status;

    private Set<UserDto> participants;
    private Boolean isPublic;
    private LocalDateTime registrationDeadline;
    private String imageUrl;
    private String externalRegistrationUrl;
    private Boolean requiresApproval;
    private Boolean isCancelled;
    private String cancellationReason;

    // Nested DTOs for related entities
    private Set<EventDonationDto> donations;

    /**
     * Checks if the event is full.
     * @return true if the event has reached maximum capacity, false otherwise
     */
    public boolean isFull() {
        return maxParticipants != null && participants != null && participants.size() >= maxParticipants;
    }

    /**
     * Checks if registration is open for the event.
     * @return true if registration is open, false otherwise
     */
    public boolean isRegistrationOpen() {
        LocalDateTime now = LocalDateTime.now();
        return registrationDeadline == null || now.isBefore(registrationDeadline);
    }

    // Validation groups for different operations
    public interface CreateValidationGroup {}
    public interface UpdateValidationGroup {}
}
