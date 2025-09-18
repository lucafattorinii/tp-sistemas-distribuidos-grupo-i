package com.empuje.userservice.service;

import com.empuje.userservice.dto.RoleDto;
import com.empuje.userservice.model.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

/**
 * Service interface for managing roles.
 */
public interface RoleService {

    /**
     * Find a role by its name
     *
     * @param name the name of the role
     * @return the role DTO
     */
    RoleDto findByName(RoleName name);

    /**
     * Find all active roles
     *
     * @return list of role DTOs
     */
    List<RoleDto> findAllActiveRoles();

    /**
     * Find roles by a set of role names
     *
     * @param names set of role names
     * @return list of role DTOs
     */
    List<RoleDto> findByNames(Set<RoleName> names);

    /**
     * Create a new role
     *
     * @param roleDto the role data
     * @return the created role DTO
     */
    RoleDto createRole(RoleDto roleDto);

    /**
     * Update an existing role
     *
     * @param name    the name of the role to update
     * @param roleDto the updated role data
     * @return the updated role DTO
     */
    RoleDto updateRole(RoleName name, RoleDto roleDto);

    /**
     * Delete a role by name
     *
     * @param name the name of the role to delete
     */
    void deleteRole(RoleName name);

    /**
     * Check if a role with the given name exists and is active
     *
     * @param name the role name to check
     * @return true if the role exists and is active, false otherwise
     */
    boolean existsByName(RoleName name);

    /**
     * Get the default role for new users
     *
     * @return the default role DTO
     */
    RoleDto getDefaultRole();

    /**
     * Search roles with pagination and filtering
     *
     * @param name     optional role name filter
     * @param active   optional active status filter
     * @param pageable pagination information
     * @return page of role DTOs
     */
    Page<RoleDto> searchRoles(RoleName name, Boolean active, Pageable pageable);

    /**
     * Count active roles
     *
     * @return the number of active roles
     */
    long countActiveRoles();
}
