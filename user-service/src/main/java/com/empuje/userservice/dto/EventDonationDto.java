package com.empuje.userservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.empuje.userservice.model.EventDonation} entity.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EventDonationDto extends BaseDto {
    @NotNull(message = "El evento es requerido")
    private EventDto event;
    
    @Size(max = 255, message = "El nombre del donante no puede exceder los 255 caracteres")
    private String donorName;
    
    private UserDto donorUser;
    
    @NotBlank(message = "El tipo de donación es requerido")
    @Size(max = 50, message = "El tipo de donación no puede exceder los 50 caracteres")
    private String donationType;
    
    @Size(max = 1000, message = "La descripción no puede exceder los 1000 caracteres")
    private String description;
    
    @PositiveOrZero(message = "El valor estimado debe ser un número positivo")
    private BigDecimal estimatedValue;
    
    @NotBlank(message = "El estado de la donación es requerido")
    @Size(max = 50, message = "El estado no puede exceder los 50 caracteres")
    private String status;
    
    private LocalDateTime deliveryDate;
    
    @Size(max = 500, message = "La dirección de entrega no puede exceder los 500 caracteres")
    private String deliveryAddress;
    
    @Size(max = 100, message = "El nombre del contacto no puede exceder los 100 caracteres")
    private String deliveryContactName;
    
    @Size(max = 20, message = "El teléfono de contacto no puede exceder los 20 caracteres")
    private String deliveryContactPhone;
    
    @Size(max = 1000, message = "Las notas de entrega no pueden exceder los 1000 caracteres")
    private String deliveryNotes;
    
    private Boolean isAnonymous;
    private Boolean receiptRequired;
    
    @Size(max = 50, message = "El número de recibo no puede exceder los 50 caracteres")
    private String receiptNumber;
    
    private LocalDateTime receiptIssuedAt;
    private Boolean acknowledgmentSent;
    private LocalDateTime acknowledgmentDate;
    
    @Size(max = 50, message = "El método de confirmación no puede exceder los 50 caracteres")
    private String acknowledgmentMethod;
    
    // Validation groups for different operations
    public interface CreateValidationGroup {}
    public interface UpdateValidationGroup {}
}
