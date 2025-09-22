package com.empuje.userservice.util;

import com.empuje.userservice.dto.UserDto;
import com.empuje.userservice.model.RoleName;
import com.empuje.userservice.model.User;
import com.empuje.userservice.grpc.gen.*;
import com.google.protobuf.Timestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Mapper utility for converting between Protobuf messages and domain objects.
 */
@Slf4j
@Component
public class ProtoMapper {

    /**
     * Convierte UserDto a UserResponse con manejo adecuado de RoleResponse y Timestamp
     */
    public static UserResponse toUserResponse(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        
        UserResponse.Builder builder = UserResponse.newBuilder()
                .setId(userDto.getId() != null ? userDto.getId() : 0)
                .setUsername(userDto.getUsername() != null ? userDto.getUsername() : "")
                .setEmail(userDto.getEmail() != null ? userDto.getEmail() : "")
                .setFirstName(userDto.getFirstName() != null ? userDto.getFirstName() : "")
                .setLastName(userDto.getLastName() != null ? userDto.getLastName() : "")
                .setPhone(safeGetString(userDto.getPhone()))
                .setAddress(safeGetString(userDto.getAddress()))
                .setIsActive(userDto.getActive() != null && userDto.getActive())
                .setEmailVerified(userDto.getEmailVerified() != null && userDto.getEmailVerified());
        
        // Mapeo del Rol
        if (userDto.getRole() != null) {
            builder.setRole(buildRoleResponse(userDto.getRole()));
        }
        
        // Manejo de marcas de tiempo
        // Handle last login if present
        if (userDto.getLastLogin() != null) {
            builder.setLastLogin(toTimestamp(userDto.getLastLogin()));
        }
        
        return builder.build();
    }

    /**
     * Convierte CreateUserRequest a UserDto con validación segura del rol
     */
    public UserDto toUserDto(CreateUserRequest request) {
        Objects.requireNonNull(request, "CreateUserRequest cannot be null");
        
        com.empuje.userservice.grpc.gen.SystemRole role = com.empuje.userservice.grpc.gen.SystemRole.ROLE_DONANTE;
        
        // Convert role from string to SystemRole
        if (request.getRole() != null && !request.getRole().isEmpty()) {
            try {
                // First convert the role string to RoleName
                RoleName roleName = RoleName.valueOf(request.getRole().toUpperCase());
                // Then convert RoleName to SystemRole
                role = com.empuje.userservice.grpc.gen.SystemRole.valueOf(roleName.name());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid role provided in CreateUserRequest: {}", request.getRole(), e);
                // Use a default role
                role = com.empuje.userservice.grpc.gen.SystemRole.ROLE_DONANTE;
            }
        }
        
        return UserDto.builder()
                .username(safeGetString(request.getUsername()))
                .email(safeGetString(request.getEmail()))
                .firstName(safeGetString(request.getFirstName()))
                .lastName(safeGetString(request.getLastName()))
                .dni("") // DNI not in CreateUserRequest
                .phone(safeGetString(request.getPhone()))
                .address(safeGetString(request.getAddress()))
                .role(role)
                .active(true) // New users are active by default
                .build();
    }

    /**
     * Convierte UpdateUserRequest a UserDto con validación segura del rol
     */
    public UserDto toUserDto(UpdateUserRequest request) {
        Objects.requireNonNull(request, "UpdateUserRequest cannot be null");
        
        UserDto.UserDtoBuilder builder = UserDto.builder()
                .id(request.getId())
                .active(request.getIsActive());
        
        // Set fields only if they are not null or empty
        if (request.getUsername() != null) builder.username(request.getUsername());
        if (request.getEmail() != null) builder.email(request.getEmail());
        if (request.getFirstName() != null) builder.firstName(request.getFirstName());
        if (request.getLastName() != null) builder.lastName(request.getLastName());
        if (request.getPhone() != null) builder.phone(request.getPhone());
        if (request.getAddress() != null) builder.address(request.getAddress());
        
        // Handle role if provided
        if (request.getRole() != null && !request.getRole().isEmpty()) {
            try {
                // Convert the string role to RoleName and then to SystemRole
                String roleStr = request.getRole().toUpperCase();
                RoleName roleName = RoleName.valueOf(roleStr);
                SystemRole systemRole = SystemRole.valueOf(roleName.name());
                builder.role(systemRole);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid role provided in UpdateUserRequest: {}", request.getRole(), e);
            }
        }
        
        return builder.build();
    }

    /**
     * Builds a RoleResponse from a SystemRole enum
     */
    private static RoleResponse buildRoleResponse(com.empuje.userservice.grpc.gen.SystemRole systemRole) {
        if (systemRole == null) {
            return RoleResponse.newBuilder()
                    .setId(com.empuje.userservice.grpc.gen.SystemRole.ROLE_UNSPECIFIED.getNumber())
                    .setName("")
                    .build();
        }
            
        return RoleResponse.newBuilder()
                .setId(systemRole.getNumber())
                .setName(systemRole.name())
                .build();
    }
    
    /**
     * Convierte un LocalDateTime a Timestamp de protobuf
     */
    public static Timestamp toTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
    
    /**
     * Convierte un Instant a Timestamp de protobuf
     */
    public static Timestamp toTimestamp(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
    
    /**
     * Safely gets a string value, returning empty string if null
     */
    private static String safeGetString(String value) {
        return value != null ? value : "";
    }
    
    // Helper methods are now inlined where needed
}
