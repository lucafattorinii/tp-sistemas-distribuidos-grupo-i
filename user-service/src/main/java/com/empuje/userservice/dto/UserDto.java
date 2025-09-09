package com.empuje.userservice.dto;

import com.empuje.userservice.model.Role;
import com.empuje.userservice.model.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private Long id;
    
    @NotBlank(message = "El nombre de usuario es requerido")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String username;
    
    @NotBlank(message = "El correo electrónico es requerido")
    @Email(message = "El correo electrónico debe ser válido")
    @Size(max = 255, message = "El correo electrónico no puede exceder los 255 caracteres")
    private String email;
    
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String firstName;
    
    @NotBlank(message = "El apellido es requerido")
    @Size(max = 100, message = "El apellido no puede exceder los 100 caracteres")
    private String lastName;
    
    @Size(max = 20, message = "El DNI no puede exceder los 20 caracteres")
    private String dni;
    
    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    private String phone;
    
    @Size(max = 500, message = "La dirección no puede exceder los 500 caracteres")
    private String address;
    
    private String profileImage;
    
    @NotNull(message = "El rol es requerido")
    private Integer roleId;
    
    private String roleName;
    
    @Enumerated(EnumType.STRING)
    private com.empuje.userservice.model.RoleName role;
    
    private LocalDateTime lastLogin;
    private boolean active = true;
    private boolean emailVerified = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Mapeo desde la entidad User
    public static UserDto fromEntity(User user) {
        if (user == null) {
            return null;
        }
        
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setDni(user.getDni());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setProfileImage(user.getProfileImage());
        
        if (user.getRole() != null) {
            dto.setRoleId(user.getRole().getId());
            dto.setRoleName(user.getRole().getName().name());
            dto.setRole(user.getRole().getName());
        }
        
        dto.setLastLogin(user.getLastLogin());
        dto.setActive(user.isActive());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        return dto;
    }
}
