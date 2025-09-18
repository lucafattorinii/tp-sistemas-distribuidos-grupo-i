package com.empuje.userservice.mapper;

import com.empuje.userservice.dto.UserDto;
import com.empuje.userservice.model.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for the entity {@link User} and its DTO {@link UserDto}.
 */
@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper extends BaseMapper<User, UserDto> {
    
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    
    @Override
    @Mapping(target = "password", ignore = true) // Never map password from entity to DTO
    @Mapping(target = "verificationToken", ignore = true) // Hide sensitive data
    @Mapping(source = "role", target = "roleDetails")
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
    User toEntity(UserDto userDto);
    
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "email", ignore = true) // Email should not be updated this way
    @Mapping(target = "password", ignore = true) // Password updates should use a dedicated method
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
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProfileFromDto(UserDto userDto, @MappingTarget User user);
}
