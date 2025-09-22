package com.empuje.userservice.service;

import com.empuje.userservice.dto.JwtAuthenticationResponse;
import com.empuje.userservice.dto.LoginRequest;
import com.empuje.userservice.dto.RoleDto;
import com.empuje.userservice.dto.UserDto;
import com.empuje.userservice.grpc.gen.SystemRole;
import com.empuje.userservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

/**
 * Service interface for managing users.
 */
public interface UserService {
    
    /**
     * Create a new user
     *
     * @param userDto  the user data
     * @param createdBy ID of the user creating this user
     * @return the created user DTO
     */
    UserDto createUser(UserDto userDto, Long createdBy);
    
    /**
     * Update an existing user
     *
     * @param id        the ID of the user to update
     * @param userDto   the updated user data
     * @param updatedBy ID of the user performing the update
     * @return the updated user DTO
     */
    UserDto updateUser(Long id, UserDto userDto, Long updatedBy);
    
    /**
     * Get a user by ID
     *
     * @param id the user ID
     * @return the user DTO
     */
    UserDto getUserById(Long id);
    
    /**
     * Get all users with pagination
     *
     * @param pageable pagination information
     * @return page of user DTOs
     */
    Page<UserDto> getAllUsers(Pageable pageable);
    
    /**
     * Delete a user (soft delete)
     *
     * @param id        the ID of the user to delete
     * @param deletedBy ID of the user performing the deletion
     */
    void deleteUser(Long id, Long deletedBy);
    
    /**
     * Activate or deactivate a user
     *
     * @param id        the ID of the user to update
     * @param active    true to activate, false to deactivate
     * @param updatedBy ID of the user performing the update
     * @return the updated user DTO
     */
    UserDto activateUser(Long id, boolean active, Long updatedBy);
    
    /**
     * Authenticate a user
     *
     * @param loginRequest the login request containing username/email and password
     * @return JWT authentication response with token and user details
     */
    JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest);
    
    /**
     * Generate a random password
     *
     * @return the generated password
     */
    String generateRandomPassword();
    
    /**
     * Send a password reset email to the user
     *
     * @param user         the user to send the email to
     * @param plainPassword the plain text password (only used for new accounts)
     */
    void sendPasswordEmail(User user, String plainPassword);
    
    /**
     * Check if a username already exists
     *
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if an email already exists
     *
     * @param email the email to check
     * @return true if the email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Find users by role name
     *
     * @param roleName the role name to filter by
     * @return list of users with the specified role
     */
    List<User> findByRole(SystemRole role);
    
    /**
     * Get the currently authenticated user
     *
     * @return the current user entity
     */
    User getCurrentUser();
    
    /**
     * Update a user's roles
     *
     * @param userId   the ID of the user to update
     * @param roleName the new role name
     * @param updatedBy ID of the user performing the update
     * @return the updated user DTO
     */
    UserDto updateUserRole(Long userId, SystemRole role, Long updatedBy);
    
    /**
     * Get all roles assigned to a user
     *
     * @param userId the user ID
     * @return set of role DTOs
     */
    Set<RoleDto> getUserRoles(Long userId);
    
    /**
     * Check if a user has a specific role
     *
     * @param userId   the user ID
     * @param roleName the role name to check
     * @return true if the user has the role, false otherwise
     */
    boolean hasRole(Long userId, SystemRole role);
    
    /**
     * Reset a user's password
     *
     * @param userId      the user ID
     * @param newPassword the new password (plain text)
     * @param updatedBy   ID of the user performing the reset
     */
    void resetPassword(Long userId, String newPassword, Long updatedBy);
    
    /**
     * Request a password reset for a user by email
     *
     * @param email the user's email address
     * @return true if the request was processed successfully
     */
    boolean requestPasswordReset(String email);
    
    /**
     * Verify a user's email address
     *
     * @param token the verification token
     * @return true if the email was verified successfully
     */
    boolean verifyEmail(String token);
}
