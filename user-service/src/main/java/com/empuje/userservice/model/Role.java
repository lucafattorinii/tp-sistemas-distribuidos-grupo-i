package com.empuje.userservice.model;

import com.empuje.userservice.grpc.gen.SystemRole;
import com.empuje.userservice.model.converter.SystemRoleConverter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Role entity representing user roles and their permissions.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Role extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Convert(converter = SystemRoleConverter.class)
    @Column(nullable = false, unique = true, length = 50)
    private SystemRole name;
    
    @Column(length = 255)
    private String description;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String permissions;
    
    @Transient
    private Map<String, Object> permissionsMap;
    
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
    
    /**
     * Gets permissions as a map.
     * @return Map of permissions
     */
    public Map<String, Object> getPermissionsMap() {
        if (this.permissionsMap == null && this.permissions != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                this.permissionsMap = mapper.readValue(
                    this.permissions, 
                    new TypeReference<Map<String, Object>>() {}
                );
            } catch (IOException e) {
                log.error("Error parsing permissions JSON", e);
                this.permissionsMap = new HashMap<>();
            }
        }
        return this.permissionsMap;
    }
    
    /**
     * Sets permissions from a map.
     * @param permissionsMap Map of permissions
     */
    public void setPermissionsMap(Map<String, Object> permissionsMap) {
        this.permissionsMap = permissionsMap;
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.permissions = mapper.writeValueAsString(permissionsMap);
        } catch (Exception e) {
            log.error("Error converting permissions to JSON", e);
            this.permissions = "{}";
        }
    }
    
    @PrePersist
    @PreUpdate
    private void prePersist() {
        if (this.permissionsMap != null) {
            setPermissionsMap(this.permissionsMap);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return id != null && id.equals(role.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "Role{" +
               "id=" + id +
               ", name=" + name +
               ", description='" + description + '\'' +
               ", active=" + active +
               '}';
    }
}
