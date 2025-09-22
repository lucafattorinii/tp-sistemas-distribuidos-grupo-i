package com.empuje.userservice.mapper;

import com.empuje.userservice.dto.UserDto;
import com.empuje.userservice.model.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Mapper for the entity {@link User} and its DTO {@link UserDto}.
 */
@Mapper(
    config = MapperConfig.class,
    uses = {RoleMapper.class, DateMapper.class},
    implementationName = "UserMapperImpl",
    implementationPackage = "<PACKAGE_NAME>.mapper.impl"
)
public interface UserMapper extends BaseMapper<User, UserDto> {
    
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    
    @Override
    @Mapping(target = "password", ignore = true) // Never map password from entity to DTO
    @Mapping(target = "verificationToken", ignore = true) // Hide sensitive data
    @Mapping(source = "role", target = "roleDetails")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "lastLogin", source = "lastLogin")
    UserDto toDto(User user);
    
    @Override
    @Mapping(target = "id", ignore = true) // ID is managed by the database
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "active", constant = "true")
    @Mapping(source = "roleDetails", target = "role")
    @Mapping(target = "lastLogin", ignore = true) // Managed by service
    User toEntity(UserDto userDto);
    
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "email", ignore = true) // Email should not be updated this way
    @Mapping(target = "password", ignore = true) // Password updates should use a dedicated method
    @Mapping(target = "lastLogin", ignore = true) // Managed by service
    @Mapping(source = "roleDetails", target = "role")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UserDto userDto, @MappingTarget User user);
    
    /**
     * Updates only the user profile information (name, phone, etc.)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "verificationToken", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProfileFromDto(UserDto userDto, @MappingTarget User user);
    
    /**
     * Maps a LocalDateTime to an Instant
     */
    default Instant map(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.atZone(ZoneId.systemDefault()).toInstant() : null;
    }
    
    /**
     * Maps an Instant to a LocalDateTime
     */
    default LocalDateTime map(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneId.systemDefault()) : null;
    }
}
