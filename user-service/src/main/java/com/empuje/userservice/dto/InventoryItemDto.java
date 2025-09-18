package com.empuje.userservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * DTO for {@link com.empuje.userservice.model.InventoryItem} entity.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryItemDto extends BaseDto {
    @NotBlank(message = "El nombre del ítem es requerido")
    @Size(max = 200, message = "El nombre no puede exceder los 200 caracteres")
    private String name;
    
    @Size(max = 1000, message = "La descripción no puede exceder los 1000 caracteres")
    private String description;
    
    @NotNull(message = "La categoría es requerida")
    private InventoryCategoryDto category;
    
    @NotBlank(message = "La unidad de medida es requerida")
    @Size(max = 50, message = "La unidad de medida no puede exceder los 50 caracteres")
    private String unitOfMeasure;
    
    @NotNull(message = "La cantidad actual es requerida")
    @DecimalMin(value = "0.0", message = "La cantidad no puede ser negativa")
    private BigDecimal currentQuantity;
    
    @NotNull(message = "La cantidad mínima es requerida")
    @DecimalMin(value = "0.0", message = "La cantidad no puede ser negativa")
    private BigDecimal minimumQuantity;
    
    private Boolean active;
    private Boolean deleted;
    
    // Validation groups for different operations
    public interface CreateValidationGroup {}
    public interface UpdateValidationGroup {}
    
    /**
     * Checks if the item is below the minimum quantity.
     * @return true if current quantity is below minimum, false otherwise
     */
    public boolean isLowStock() {
        return currentQuantity.compareTo(minimumQuantity) <= 0;
    }
}
