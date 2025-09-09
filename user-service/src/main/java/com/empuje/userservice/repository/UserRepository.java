package com.empuje.userservice.repository;

import com.empuje.userservice.model.Role;
import com.empuje.userservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.username = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);
    
    Page<User> findAll(Pageable pageable);
    
    List<User> findByRole(Role role);
    
    @Query("SELECT u FROM User u WHERE u.active = :active")
    Page<User> findByActive(@Param("active") boolean active, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.role.name = :roleName")
    List<User> findByRoleName(@Param("roleName") Role.RoleName roleName);
}
