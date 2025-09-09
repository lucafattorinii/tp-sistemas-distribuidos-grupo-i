package com.empuje.userservice.util;

import com.empuje.userservice.dto.UserDto;
import com.empuje.userservice.model.Role;
import com.empuje.userservice.model.RoleName;
import com.empuje.userservice.model.User;
import com.empuje.userservice.grpc.gen.*;
import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ProtoMapper {

    /**
     * Convierte el modelo de dominio User a UserResponse de gRPC con manejo adecuado de RoleResponse y Timestamp
     */
    public UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }
        
        UserResponse.Builder builder = UserResponse.newBuilder()
                .setId(user.getId())
                .setUsername(user.getUsername() != null ? user.getUsername() : "")
                .setEmail(user.getEmail() != null ? user.getEmail() : "")
                .setFirstName(user.getFirstName() != null ? user.getFirstName() : "")
                .setLastName(user.getLastName() != null ? user.getLastName() : "")
                .setDni(user.getDni() != null ? user.getDni() : "")
                .setPhone(user.getPhone() != null ? user.getPhone() : "")
                .setAddress(user.getAddress() != null ? user.getAddress() : "")
                .setActive(user.isActive());
                
        // Mapeo del Rol
        if (user.getRole() != null) {
            builder.setRole(buildRoleResponse(user.getRole()));
        }
        
 // Manejo de marcas de tiempo
        if (user.getCreatedAt() != null) {
            builder.setCreatedAt(toTimestamp(user.getCreatedAt()));
        }
        
        if (user.getUpdatedAt() != null) {
            builder.setUpdatedAt(toTimestamp(user.getUpdatedAt()));
        }
        
        if (user.getLastLogin() != null) {
            builder.setLastLogin(toTimestamp(user.getLastLogin()));
        }
        
        return builder.build();
    }

    /**
     * Convierte CreateUserRequest a UserDto con validación segura del rol
     */
    public UserDto toUserDto(CreateUserRequest request) {
        RoleName role = RoleName.ROLE_DONANTE; // Rol por defecto
        
        // Parseo seguro del rol
        if (request.hasRole() && !request.getRole().isEmpty()) {
            try {
                role = RoleName.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid role provided: {}. Using default role: {}", 
                        request.getRole(), role);
            }
        }
        
        return UserDto.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.hasFirstName() ? request.getFirstName() : "")
                .lastName(request.hasLastName() ? request.getLastName() : "")
                .dni(request.hasDni() ? request.getDni() : "")
                .phone(request.hasPhone() ? request.getPhone() : "")
                .address(request.hasAddress() ? request.getAddress() : "")
                .role(role)
                .active(true) // Los nuevos usuarios están activos por defecto
                .build();
    }

    /**
     * Convierte UpdateUserRequest a UserDto con validación segura del rol
     */
    public UserDto toUserDto(UpdateUserRequest request) {
        RoleName role = RoleName.ROLE_DONANTE; // Rol por defecto
        
        // Parseo seguro del rol
        if (request.hasRole() && !request.getRole().isEmpty()) {
            try {
                role = RoleName.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid role provided: {}. Using default role: {}", 
                        request.getRole(), role);
            }
        }
        
        return UserDto.builder()
                .id(request.getId())
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.hasFirstName() ? request.getFirstName() : "")
                .lastName(request.hasLastName() ? request.getLastName() : "")
                .dni(request.hasDni() ? request.getDni() : "")
                .phone(request.hasPhone() ? request.getPhone() : "")
                .address(request.hasAddress() ? request.getAddress() : "")
                .role(role)
                .active(request.getActive())
                .build();
    }

    /**
     * Convierte UserDto a UserResponse con manejo adecuado de RoleResponse y Timestamp
     */
    public UserResponse toUserResponse(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        
        UserResponse.Builder builder = UserResponse.newBuilder()
                .setId(userDto.getId())
                .setUsername(userDto.getUsername() != null ? userDto.getUsername() : "")
                .setEmail(userDto.getEmail() != null ? userDto.getEmail() : "")
                .setFirstName(userDto.getFirstName() != null ? userDto.getFirstName() : "")
                .setLastName(userDto.getLastName() != null ? userDto.getLastName() : "")
                .setDni(userDto.getDni() != null ? userDto.getDni() : "")
                .setPhone(userDto.getPhone() != null ? userDto.getPhone() : "")
                .setAddress(userDto.getAddress() != null ? userDto.getAddress() : "")
                .setActive(userDto.isActive());
        
        // Mapeo del Rol
        if (userDto.getRole() != null) {
            builder.setRole(RoleResponse.newBuilder()
                    .setName(userDto.getRole().name())
                    .setIsActive(true)
                    .build());
        }
        
 // Manejo de marcas de tiempo
        if (userDto.getCreatedAt() != null) {
            builder.setCreatedAt(toTimestamp(userDto.getCreatedAt()));
        }
        
        if (userDto.getUpdatedAt() != null) {
            builder.setUpdatedAt(toTimestamp(userDto.getUpdatedAt()));
        }
        
        if (userDto.getLastLogin() != null) {
            builder.setLastLogin(toTimestamp(userDto.getLastLogin()));
        }
        
        return builder.build();
    }
}
