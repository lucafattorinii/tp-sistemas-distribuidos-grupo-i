package com.empuje.userservice.model;

import com.empuje.userservice.grpc.gen.SystemRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

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
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
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

    @Transient
    private String roleName; // Transient field to expose role name in JSON

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
    private LocalDateTime lastLoginDate;
    
    @Column(name = "last_password_reset_date")
    private Instant lastPasswordResetDate;
    
    public LocalDateTime getLastLogin() {
        return lastLoginDate;
    }
    
    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
    
    /**
     * Sets the last login timestamp using an Instant.
     * @param lastLogin the instant when the user last logged in
     */
    public void setLastLogin(Instant lastLogin) {
        this.lastLoginDate = lastLogin != null ? lastLogin.atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
    }
    
    public Instant getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }
    
    public void setLastPasswordResetDate(Instant lastPasswordResetDate) {
        this.lastPasswordResetDate = lastPasswordResetDate;
    }
    
    /**
     * Sets the password reset token expiry using an Instant.
     * @param expiry the instant when the password reset token expires
     */
    public void setPasswordResetTokenExpiry(Instant expiry) {
        this.passwordResetExpires = expiry != null ? expiry.atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // To handle lazy loading
    private Role role;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "account_non_expired", nullable = false)
    private boolean accountNonExpired = true;

    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked = true;

    @Column(name = "credentials_non_expired", nullable = false)
    private boolean credentialsNonExpired = true;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null || role.getName() == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.getName().name()));
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
    
    @Override
    public boolean isEnabled() {
        return this.active && this.enabled;
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
        
        public Builder role(Role role) {
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
        
        public Builder phone(String phone) {
            user.setPhone(phone);
            return this;
        }
        
        public Builder address(String address) {
            user.setAddress(address);
            return this;
        }
        
        public Builder profileImage(String profileImage) {
            user.setProfileImage(profileImage);
            return this;
        }
        
        public Builder profileImageUrl(String profileImageUrl) {
            user.setProfileImageUrl(profileImageUrl);
            return this;
        }
        
        public Builder verificationToken(String verificationToken) {
            user.setVerificationToken(verificationToken);
            return this;
        }
        
        public Builder passwordResetToken(String passwordResetToken) {
            user.setPasswordResetToken(passwordResetToken);
            return this;
        }
        
        public Builder passwordResetExpires(LocalDateTime passwordResetExpires) {
            user.setPasswordResetExpires(passwordResetExpires);
            return this;
        }
        
        public Builder lastLogin(LocalDateTime lastLogin) {
            user.setLastLoginDate(lastLogin);
            return this;
        }
        
        public User build() {
            // Any default values can be set here if needed
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
     * Gets the name of the user's role.
     * @return the role name or empty string if not set
     */
    public String getRoleName() {
        if (roleName == null && role != null && role.getName() != null) {
            roleName = role.getName().name();
        }
        return roleName != null ? roleName : "";
    }

    /**
     * Checks if the user has the specified role.
     * @param systemRole the role to check
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(SystemRole systemRole) {
        return role != null && role.getName() != null && role.getName().equals(systemRole);
    }

    /**
     * Returns the user's full name by combining first and last name
     * @return the full name of the user
     */
    public String getFullName() {
        return (firstName != null ? firstName : "") + 
               (lastName != null ? " " + lastName : "").trim();
    }
    
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
               ", role=" + (role != null && role.getName() != null ? role.getName().name() : "null") +
               ", enabled=" + enabled +
               '}';
    }
}
