package com.empuje.userservice.mapper;

import com.empuje.userservice.dto.InventoryCategoryDto;
import com.empuje.userservice.model.InventoryCategory;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for the entity {@link InventoryCategory} and its DTO {@link InventoryCategoryDto}.
 */
@Mapper(componentModel = "spring")
public interface InventoryCategoryMapper extends BaseMapper<InventoryCategory, InventoryCategoryDto> {
    
    InventoryCategoryMapper INSTANCE = Mappers.getMapper(InventoryCategoryMapper.class);
    
    @Override
    @Mapping(target = "active", source = "active", defaultValue = "true")
    InventoryCategoryDto toDto(InventoryCategory category);
    
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", source = "active", defaultValue = "true")
    InventoryCategory toEntity(InventoryCategoryDto categoryDto);
    
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(InventoryCategoryDto categoryDto, @MappingTarget InventoryCategory category);
}
