package com.empuje.userservice.service.impl;

import com.empuje.userservice.dto.RoleDto;
import com.empuje.userservice.exception.ResourceAlreadyExistsException;
import com.empuje.userservice.exception.ResourceNotFoundException;
import com.empuje.userservice.mapper.RoleMapper;
import com.empuje.userservice.model.Role;
import com.empuje.userservice.grpc.gen.SystemRole;
import com.empuje.userservice.repository.RoleRepository;
import com.empuje.userservice.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link RoleService} interface.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleDto findByName(SystemRole name) {
        log.debug("Finding role by name: {}", name);
        return roleRepository.findByName(name)
                .map(roleMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", name));
    }

    @Override
    public List<RoleDto> findAllActiveRoles() {
        log.debug("Finding all active roles");
        return roleRepository.findByActiveTrue().stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleDto> findByNames(Set<SystemRole> names) {
        log.debug("Finding roles by names: {}", names);
        return roleRepository.findByNames(names).stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoleDto createRole(RoleDto roleDto) {
        log.debug("Creating new role: {}", roleDto.getName());
        
        // Check if role with this name already exists
        roleRepository.findByName(roleDto.getName())
                .ifPresent(role -> {
                    throw new ResourceAlreadyExistsException("Role", "name", roleDto.getName().name());
                });
        
        Role role = roleMapper.toEntity(roleDto);
        role.setActive(true);
        Role savedRole = roleRepository.save(role);
        log.info("Created role with name: {}", savedRole.getName());
        
        return roleMapper.toDto(savedRole);
    }

    @Override
    @Transactional
    public RoleDto updateRole(SystemRole name, RoleDto roleDto) {
        log.debug("Updating role: {}", name);
        
        Role existingRole = roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", name));
        
        // Update fields if they are not null in the DTO
        if (roleDto.getDescription() != null) {
            existingRole.setDescription(roleDto.getDescription());
        }
        
        if (roleDto.getPermissions() != null) {
            existingRole.setPermissionsMap(roleDto.getPermissions());
        }
        
        if (roleDto.getActive() != null) {
            existingRole.setActive(roleDto.getActive());
        }
        
        Role updatedRole = roleRepository.save(existingRole);
        log.info("Updated role: {}", updatedRole.getName());
        
        return roleMapper.toDto(updatedRole);
    }

    @Override
    @Transactional
    public void deleteRole(SystemRole name) {
        log.debug("Deleting role: {}", name);
        
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", name));
        
        // Instead of deleting, we'll deactivate the role
        role.setActive(false);
        roleRepository.save(role);
        log.info("Deactivated role: {}", name);
    }

    @Override
    public boolean existsByName(SystemRole name) {
        return roleRepository.existsByNameAndActiveTrue(name);
    }

    @Override
    public RoleDto getDefaultRole() {
        log.debug("Getting default role");
        // Default role is ROLE_DONANTE
        return roleRepository.findByName(SystemRole.ROLE_DONANTE)
                .map(roleMapper::toDto)
                .orElseThrow(() -> new IllegalStateException("Default role ROLE_DONANTE not found"));
    }

    @Override
    public Page<RoleDto> searchRoles(SystemRole name, Boolean active, Pageable pageable) {
        log.debug("Searching roles with name: {}, active: {}", name, active);
        List<Role> allRoles = roleRepository.searchRoles(name, active);
        
        // Apply pagination manually
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allRoles.size());
        
        if (start > allRoles.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, allRoles.size());
        }
        
        List<Role> pageContent = allRoles.subList(start, end);
        return new PageImpl<>(
            pageContent.stream().map(roleMapper::toDto).collect(Collectors.toList()),
            pageable,
            allRoles.size()
        );
    }

    @Override
    public long countActiveRoles() {
        return roleRepository.countByActiveTrue();
    }
}
