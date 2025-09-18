package com.empuje.userservice.model;

import com.empuje.userservice.grpc.gen.SystemRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * User entity that implements Spring Security's UserDetails interface.
 * Represents a user in the system with authentication and authorization details.
 */
@Entity
@Table(name = "users", 
       indexes = {
           @Index(name = "idx_user_email", columnList = "email"),
           @Index(name = "idx_user_username", columnList = "username")
       })
@SQLDelete(sql = "UPDATE users SET is_active = false WHERE id = ?")
@Where(clause = "is_active = true")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity implements UserDetails {
    
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @NaturalId
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String password;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "dni", length = 20)
    private String dni;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "profile_image")
    private String profileImage;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "is_email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "verification_token", length = 64)
    private String verificationToken;

    @Column(name = "password_reset_token", length = 64)
    private String passwordResetToken;

    @Column(name = "password_reset_expires")
    private LocalDateTime passwordResetExpires;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    /**
     * Returns the authorities granted to the user.
     * 
     * @return a collection of authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role != null 
            ? Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
            : Collections.emptyList();
    }
    
    /**
     * Get the user's role name as a string.
     * 
     * @return Role name as string (e.g., "ROLE_ADMIN")
     */
    public String getRoleName() {
        return role != null ? role.getName().name() : "";
    }

    /**
     * Indicates whether the user's account has expired.
     * 
     * @return true if the user's account is valid (ie non-expired), false if no longer valid (ie expired)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     * 
     * @return true if the user is not locked, false otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired.
     * 
     * @return true if the user's credentials are valid (ie non-expired), false if no longer valid (ie expired)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     * 
     * @return true if the user is enabled, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
    
    @Column(length = 20)
    private String phone;
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    @Column(length = 500)
    private String address;
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    @Column(name = "profile_image", length = 255)
    private String profileImage;
    
    public String getProfileImage() {
        return profileImage;
    }
    
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
    
    @Override
    public boolean isEnabled() {
        return this.active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;
    
    public boolean isEmailVerified() {
        return emailVerified;
    }
    
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SystemRole role;
    
    public SystemRole getRole() {
        return role;
    }
    
    public void setRole(SystemRole role) {
        this.role = role;
    }

    @Column(name = "verification_token", length = 64)
    private String verificationToken;
    
    public String getVerificationToken() {
        return verificationToken;
    }
    
    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    @Column(name = "password_reset_token", length = 64)
    private String passwordResetToken;
    
    public String getPasswordResetToken() {
        return passwordResetToken;
    }
    
    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    @Column(name = "password_reset_expires")
    private LocalDateTime passwordResetExpires;
    
    public LocalDateTime getPasswordResetExpires() {
        return passwordResetExpires;
    }
    
    public void setPasswordResetExpires(LocalDateTime passwordResetExpires) {
        this.passwordResetExpires = passwordResetExpires;
    }

    @Column(name = "account_non_expired", nullable = false)
    private boolean accountNonExpired = true;
    
    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked = true;
    
    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @Column(name = "credentials_non_expired", nullable = false)
    private boolean credentialsNonExpired = true;
    
    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }


    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    
    // Builder pattern implementation
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final User user = new User();
        
        public Builder id(Long id) {
            user.setId(id);
            return this;
        }
        
        public Builder username(String username) {
            user.setUsername(username);
            return this;
        }
        
        public Builder email(String email) {
            user.setEmail(email);
            return this;
        }
        
        public Builder password(String password) {
            user.setPassword(password);
            return this;
        }
        
        public Builder firstName(String firstName) {
            user.setFirstName(firstName);
            return this;
        }
        
        public Builder lastName(String lastName) {
            user.setLastName(lastName);
            return this;
        }
        
        public Builder role(SystemRole role) {
            user.setRole(role);
            return this;
        }
        
        public Builder active(boolean active) {
            user.setActive(active);
            return this;
        }
        
        public Builder emailVerified(boolean emailVerified) {
            user.setEmailVerified(emailVerified);
            return this;
        }
        
        public User build() {
            // Set default values if not set
            if (user.getRole() == null) {
                user.setRole(SystemRole.VOLUNTARIO);
            }
            return user;
        }
    }

    /**
     * Checks if the user account is fully active.
     * @return true if the account is enabled and not expired, locked, or credentials expired
     */
    public boolean isActive() {
        return isEnabled() && isAccountNonExpired() && isAccountNonLocked() && isCredentialsNonExpired();
    }

    /**
     * Gets the user's full name.
     * @return the concatenated first and last name
     */
    public String getFullName() {
        return String.format("%s %s", firstName, lastName).trim();
    }

    /**
     * Checks if the user has the specified role.
     * @param roleName the role to check
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(SystemRole systemRole) {
        return role != null && role == systemRole;
    }

    /**
     * Gets the user's role name.
     * @return the role name or null if no role is set
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
               Objects.equals(username, user.username) &&
               Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", role=" + (role != null ? role.name() : "null") +
               ", enabled=" + enabled +
               '}';
    }
}
