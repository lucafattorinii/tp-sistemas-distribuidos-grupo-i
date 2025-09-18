package com.empuje.userservice.mapper;

import com.empuje.userservice.dto.BaseDto;
import com.empuje.userservice.model.BaseEntity;

/**
 * Base mapper interface for entity-DTO mapping.
 *
 * @param <E> Entity type
 * @param <D> DTO type
 */
public interface BaseMapper<E extends BaseEntity, D extends BaseDto> {
    
    /**
     * Convert entity to DTO.
     *
     * @param entity the entity to convert
     * @return the DTO
     */
    D toDto(E entity);
    
    /**
     * Convert DTO to entity.
     *
     * @param dto the DTO to convert
     * @return the entity
     */
    E toEntity(D dto);
    
    /**
     * Update entity from DTO.
     *
     * @param dto the DTO with updated values
     * @param entity the entity to update
     */
    void updateFromDto(D dto, E entity);
}
