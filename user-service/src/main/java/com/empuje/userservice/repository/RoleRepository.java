package com.empuje.userservice.repository;

import com.empuje.userservice.model.Role;
import com.empuje.userservice.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Find a role by its name
     */
    Optional<Role> findByName(RoleName name);
    
    /**
     * Find all active roles
     */
    List<Role> findByActiveTrue();
    
    /**
     * Find roles by a set of role names
     */
    @Query("SELECT r FROM Role r WHERE r.name IN :names AND r.active = true")
    List<Role> findByNames(@Param("names") Set<RoleName> names);
    
    /**
     * Check if a role with the given name exists and is active
     */
    boolean existsByNameAndActiveTrue(RoleName name);
    
    /**
     * Count active roles
     */
    long countByActiveTrue();
    
    /**
     * Find all roles with pagination and filtering
     */
    @Query("SELECT r FROM Role r WHERE " +
           "(:name IS NULL OR r.name = :name) AND " +
           "(:active IS NULL OR r.active = :active)")
    List<Role> searchRoles(
        @Param("name") RoleName name,
        @Param("active") Boolean active
    );
}
