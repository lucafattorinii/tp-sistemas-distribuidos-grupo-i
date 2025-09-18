package com.empuje.userservice.mapper;

import com.empuje.userservice.dto.InventoryItemDto;
import com.empuje.userservice.model.InventoryItem;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

/**
 * Mapper for the entity {@link InventoryItem} and its DTO {@link InventoryItemDto}.
 */
@Mapper(componentModel = "spring", uses = {InventoryCategoryMapper.class})
public interface InventoryItemMapper extends BaseMapper<InventoryItem, InventoryItemDto> {
    
    InventoryItemMapper INSTANCE = Mappers.getMapper(InventoryItemMapper.class);
    
    @Override
    @Mapping(target = "active", source = "active", defaultValue = "true")
    @Mapping(target = "deleted", source = "deleted", defaultValue = "false")
    @Mapping(target = "currentQuantity", source = "currentQuantity", defaultValue = "0")
    @Mapping(target = "minimumQuantity", source = "minimumQuantity", defaultValue = "0")
    InventoryItemDto toDto(InventoryItem item);
    
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", source = "active", defaultValue = "true")
    @Mapping(target = "deleted", source = "deleted", defaultValue = "false")
    @Mapping(target = "currentQuantity", source = "currentQuantity", defaultValue = "0")
    @Mapping(target = "minimumQuantity", source = "minimumQuantity", defaultValue = "0")
    InventoryItem toEntity(InventoryItemDto itemDto);
    
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "currentQuantity", ignore = true) // Quantity should be updated via specific methods
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(InventoryItemDto itemDto, @MappingTarget InventoryItem item);
    
    /**
     * Updates only the basic information of an inventory item, excluding quantity-related fields.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentQuantity", ignore = true)
    @Mapping(target = "minimumQuantity", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBasicInfo(InventoryItemDto itemDto, @MappingTarget InventoryItem item);
    
    /**
     * Updates the quantity of an inventory item.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "unitOfMeasure", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateQuantity(@MappingTarget InventoryItem item, BigDecimal newQuantity);
}
