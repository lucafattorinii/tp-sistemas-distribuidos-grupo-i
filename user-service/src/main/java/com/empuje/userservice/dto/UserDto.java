package com.empuje.userservice.dto;

import com.empuje.userservice.grpc.gen.SystemRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.empuje.userservice.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.empuje.userservice.model.User} entity.
 * This DTO is used for both API and gRPC communication.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDto extends BaseDto {
    @NotBlank(message = "El nombre de usuario es requerido", groups = {CreateValidationGroup.class})
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_\\.-]+$", 
             message = "El nombre de usuario solo puede contener letras, números, puntos, guiones y guiones bajos")
    private String username;

    @NotBlank(message = "La contraseña es requerida", groups = {CreateValidationGroup.class})
    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres", 
          groups = {CreateValidationGroup.class})
    private String password;

    @NotBlank(message = "El correo electrónico es requerido", groups = {CreateValidationGroup.class})
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

    @Pattern(regexp = "^\\+?[0-9\\s-]+$", message = "El formato del teléfono no es válido")
    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    private String phone;

    @Size(max = 500, message = "La dirección no puede exceder los 500 caracteres")
    private String address;

    private String profileImage;
    
    private String profileImageUrl;
    
    private Boolean emailVerified = false;
    
    private LocalDateTime lastLogin;
    
    private Boolean active = true;

    @NotNull(message = "El rol es requerido", groups = {CreateValidationGroup.class})
    private SystemRole role;
    
    // Nested DTO for role details
    private RoleDto roleDetails;
    
    // Internal use only - not exposed via API/gRPC
    @JsonIgnore
    private String verificationToken;
    
    // For compatibility with getter/setter naming conventions
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    /**
     * Validation group for create operations
     */
    public interface CreateValidationGroup {}

    /**
     * Validation group for update operations
     */
    public interface UpdateValidationGroup {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public SystemRole getRole() {
        return role;
    }

    public void setRole(SystemRole role) {
        this.role = role;
    }
    
    public void setRole(String roleName) {
        this.role = SystemRole.valueOf(roleName);
    }
    
    // Lombok @SuperBuilder provides the builder; remove custom builder to avoid conflicts.

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isActivePrimitive() { return Boolean.TRUE.equals(active); }
    public boolean isEmailVerified() { return Boolean.TRUE.equals(emailVerified); }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

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
        
        if (user.getRole() != null && user.getRole().getName() != null) {
            dto.setRole(user.getRole().getName());
        }
        
        dto.setLastLogin(user.getLastLogin());
        dto.setActive(user.isEnabled());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        return dto;
    }
}
