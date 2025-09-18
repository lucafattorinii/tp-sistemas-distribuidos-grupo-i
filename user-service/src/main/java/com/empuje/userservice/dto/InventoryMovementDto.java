package com.empuje.userservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.empuje.userservice.model.InventoryMovement} entity.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryMovementDto extends BaseDto {
    
    public enum MovementType {
        ENTRADA,    // Stock in
        SALIDA,     // Stock out
        AJUSTE      // Adjustment
    }

    @NotNull(message = "El ítem es requerido")
    private InventoryItemDto item;
    
    @NotNull(message = "El tipo de movimiento es requerido")
    private MovementType movementType;
    
    @NotNull(message = "La cantidad es requerida")
    @PositiveOrZero(message = "La cantidad debe ser un número positivo")
    private BigDecimal quantity;
    
    @NotNull(message = "La cantidad anterior es requerida")
    @PositiveOrZero(message = "La cantidad debe ser un número positivo")
    private BigDecimal previousQuantity;
    
    @NotNull(message = "La nueva cantidad es requerida")
    @PositiveOrZero(message = "La cantidad debe ser un número positivo")
    private BigDecimal newQuantity;
    
    @NotNull(message = "La fecha del movimiento es requerida")
    private LocalDateTime movementDate;
    
    private Long referenceId;
    private String referenceType;
    
    @Size(max = 1000, message = "Las notas no pueden exceder los 1000 caracteres")
    private String notes;
    
    private UserDto createdByUser;
    
    // Validation groups for different operations
    public interface CreateValidationGroup {}
    public interface UpdateValidationGroup {}
}
