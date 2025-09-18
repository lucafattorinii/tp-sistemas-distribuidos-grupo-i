package com.empuje.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DTO for {@link com.empuje.userservice.model.InventoryCategory} entity.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryCategoryDto extends BaseDto {
    @NotBlank(message = "El nombre de la categoría es requerido")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String name;
    
    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    private String description;
    
    private Boolean active;
    
    // Validation groups for different operations
    public interface CreateValidationGroup {}
    public interface UpdateValidationGroup {}
}
