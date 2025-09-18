package com.empuje.userservice.mapper;

import com.empuje.userservice.dto.EventStatusDto;
import com.empuje.userservice.model.EventStatus;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for the entity {@link EventStatus} and its DTO {@link EventStatusDto}.
 */
@Mapper(componentModel = "spring")
public interface EventStatusMapper extends BaseMapper<EventStatus, EventStatusDto> {
    
    EventStatusMapper INSTANCE = Mappers.getMapper(EventStatusMapper.class);
    
    @Override
    @Mapping(target = "active", source = "active", defaultValue = "true")
    EventStatusDto toDto(EventStatus eventStatus);
    
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", source = "active", defaultValue = "true")
    EventStatus toEntity(EventStatusDto eventStatusDto);
    
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(EventStatusDto eventStatusDto, @MappingTarget EventStatus eventStatus);
}
