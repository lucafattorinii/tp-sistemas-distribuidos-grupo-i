package com.empuje.userservice.mapper;

import com.empuje.userservice.dto.EventDto;
import com.empuje.userservice.model.Event;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Set;

/**
 * Mapper for the entity {@link Event} and its DTO {@link EventDto}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, EventStatusMapper.class})
public interface EventMapper extends BaseMapper<Event, EventDto> {
    
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);
    
    @Override
    @Mapping(target = "participants", source = "participants")
    @Mapping(target = "isPublic", source = "public")
    EventDto toDto(Event event);
    
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "participants", ignore = true) // Handle participants separately
    @Mapping(target = "public", source = "isPublic")
    @Mapping(target = "isCancelled", defaultValue = "false")
    Event toEntity(EventDto eventDto);
    
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "participants", ignore = true) // Handle participants separately
    @Mapping(target = "public", source = "isPublic")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(EventDto eventDto, @MappingTarget Event event);
    
    /**
     * Maps a set of participants to a set of participant DTOs.
     */
    Set<EventDto> toDtoSet(Set<Event> events);
    
    /**
     * Maps a set of participant DTOs to a set of participants.
     */
    Set<Event> toEntitySet(Set<EventDto> eventDtos);
}
