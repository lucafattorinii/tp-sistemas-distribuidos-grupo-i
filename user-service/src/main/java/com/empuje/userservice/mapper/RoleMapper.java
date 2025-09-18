package com.empuje.userservice.mapper;

import com.empuje.userservice.dto.RoleDto;
import com.empuje.userservice.model.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapper for the entity {@link Role} and its DTO {@link RoleDto}.
 */
@Mapper(componentModel = "spring")
public abstract class RoleMapper implements BaseMapper<Role, RoleDto> {
    
    public static final RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);
    
    @Autowired
    protected ObjectMapper objectMapper;
    
    @Override
    @Mapping(target = "permissionsMap", source = "permissions")
    @Mapping(target = "permissions", ignore = true)
    public abstract RoleDto toDto(Role role);
    
    @Override
    @Mapping(target = "permissions", source = "permissionsMap")
    public abstract Role toEntity(RoleDto roleDto);
    
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "permissions", source = "permissionsMap")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateFromDto(RoleDto roleDto, @MappingTarget Role role);
    
    /**
     * Converts JSON string to Map.
     */
    protected Map<String, Object> map(String json) {
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing permissions JSON", e);
        }
    }
    
    /**
     * Converts Map to JSON string.
     */
    protected String map(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting permissions to JSON", e);
        }
    }
}
