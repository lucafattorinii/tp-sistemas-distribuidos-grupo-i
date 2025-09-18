package com.empuje.userservice.mapper;

import com.empuje.userservice.dto.InventoryMovementDto;
import com.empuje.userservice.model.InventoryMovement;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

/**
 * Mapper for the entity {@link InventoryMovement} and its DTO {@link InventoryMovementDto}.
 */
@Mapper(componentModel = "spring", uses = {InventoryItemMapper.class, UserMapper.class})
public interface InventoryMovementMapper extends BaseMapper<InventoryMovement, InventoryMovementDto> {
    
    InventoryMovementMapper INSTANCE = Mappers.getMapper(InventoryMovementMapper.class);
    
    @Override
    @Mapping(target = "movementType", source = "movementType")
    @Mapping(target = "referenceId", source = "referenceId")
    @Mapping(target = "referenceType", source = "referenceType")
    InventoryMovementDto toDto(InventoryMovement movement);
    
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "movementDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "movementType", source = "movementType")
    @Mapping(target = "referenceId", source = "referenceId")
    @Mapping(target = "referenceType", source = "referenceType")
    InventoryMovement toEntity(InventoryMovementDto movementDto);
    
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "movementDate", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "previousQuantity", ignore = true)
    @Mapping(target = "newQuantity", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(InventoryMovementDto movementDto, @MappingTarget InventoryMovement movement);
    
    /**
     * Creates a new movement for an inventory adjustment.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "movementType", constant = "AJUSTE")
    @Mapping(target = "movementDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "referenceId", source = "referenceId")
    @Mapping(target = "referenceType", source = "referenceType")
    @Mapping(target = "notes", source = "notes")
    InventoryMovement createAdjustment(
            @MappingTarget InventoryItemDto itemDto, 
            BigDecimal newQuantity,
            String notes,
            Long referenceId,
            String referenceType);
    
    /**
     * Creates a new movement for stock in.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "movementType", constant = "ENTRADA")
    @Mapping(target = "movementDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "referenceId", source = "referenceId")
    @Mapping(target = "referenceType", source = "referenceType")
    @Mapping(target = "notes", source = "notes")
    InventoryMovement createStockIn(
            @MappingTarget InventoryItemDto itemDto, 
            BigDecimal quantity,
            String notes,
            Long referenceId,
            String referenceType);
    
    /**
     * Creates a new movement for stock out.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "movementType", constant = "SALIDA")
    @Mapping(target = "movementDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "referenceId", source = "referenceId")
    @Mapping(target = "referenceType", source = "referenceType")
    @Mapping(target = "notes", source = "notes")
    InventoryMovement createStockOut(
            @MappingTarget InventoryItemDto itemDto, 
            BigDecimal quantity,
            String notes,
            Long referenceId,
            String referenceType);
}
