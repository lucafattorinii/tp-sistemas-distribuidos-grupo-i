package com.empuje.userservice.service.impl;

import com.empuje.userservice.dto.JwtAuthenticationResponse;
import com.empuje.userservice.dto.LoginRequest;
import com.empuje.userservice.dto.RoleDto;
import com.empuje.userservice.dto.UserDto;
import com.empuje.userservice.exception.BadRequestException;
import com.empuje.userservice.exception.ResourceNotFoundException;
import com.empuje.userservice.exception.UnauthorizedException;
import com.empuje.userservice.grpc.gen.SystemRole;
import com.empuje.userservice.mapper.RoleMapper;
import com.empuje.userservice.mapper.UserMapper;
import com.empuje.userservice.model.Role;
import com.empuje.userservice.model.User;
import com.empuje.userservice.repository.RoleRepository;
import com.empuje.userservice.repository.UserRepository;
import com.empuje.userservice.security.JwtTokenProvider;
import com.empuje.userservice.service.EmailService;
import com.empuje.userservice.service.RoleService;
import com.empuje.userservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_SORT_FIELD = "lastModifiedDate";
    private static final String USER_NOT_FOUND = "User not found with id: %s";
    private static final String EMAIL_IN_USE = "Email is already in use by another user";
    private static final String USERNAME_IN_USE = "Username is already in use by another user";
    private static final String INVALID_CREDENTIALS = "Invalid username or password";
    private static final String ACCOUNT_DISABLED = "User account is disabled";
    private static final String EMAIL_VERIFICATION_REQUIRED = "Email verification is required";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RoleService roleService;
    
    @Lazy
    private final EmailService emailService;
    
    @Value("${app.security.jwt.expiration-in-ms:86400000}")
    private long jwtExpirationInMs;
    
    @Value("${app.security.require-email-verification:false}")
    private boolean requireEmailVerification;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto, Long createdBy) {
        log.info("Creating new user with username: {}", userDto.getUsername());
        
        // Validate input
        validateUserDto(userDto);
        
        // Check if username or email already exists
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new BadRequestException(USERNAME_IN_USE);
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new BadRequestException(EMAIL_IN_USE);
        }

        // Map DTO to entity
        User user = userMapper.toEntity(userDto);
        
        // Set password
        String password = StringUtils.hasText(userDto.getPassword()) ? 
                userDto.getPassword() : generateRandomPassword();
        user.setPassword(passwordEncoder.encode(password));
        
        // Set role
        SystemRole role = userDto.getRole() != null ? 
                userDto.getRole() : SystemRole.VOLUNTARIO;
        setUserRole(user, role);
        
        // Set audit fields
        user.setActive(true);
        user.setEmailVerified(!requireEmailVerification);
        user.setCreatedBy(createdBy);
        
        // Save user
        User savedUser = userRepository.save(user);
        log.info("Created user with id: {}", savedUser.getId());
        
        // Send welcome email with credentials if email service is available
        if (emailService != null) {
            try {
                emailService.sendUserRegistrationEmail(savedUser, password);
                
                if (requireEmailVerification) {
                    emailService.sendVerificationEmail(savedUser);
                }
            } catch (Exception e) {
                log.error("Error sending welcome email: {}", e.getMessage(), e);
            }
        }

        return userMapper.toDto(savedUser);
    }
    
    private void setUserRole(User user, SystemRole roleName) {
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName.name()));
        user.setRole(role);
    }

    private void validateUserDto(UserDto userDto) {
        Objects.requireNonNull(userDto, "User DTO cannot be null");
        
        if (!StringUtils.hasText(userDto.getUsername())) {
            throw new BadRequestException("Username is required");
        }
        
        if (!StringUtils.hasText(userDto.getEmail())) {
            throw new BadRequestException("Email is required");
        }
        
        if (!StringUtils.hasText(userDto.getFirstName())) {
            throw new BadRequestException("First name is required");
        }
        
        if (!StringUtils.hasText(userDto.getLastName())) {
            throw new BadRequestException("Last name is required");
        }
        
        // Validate email format
        if (!isValidEmail(userDto.getEmail())) {
            throw new BadRequestException("Invalid email format");
        }
    }
    
    private boolean isValidEmail(String email) {
        // Simple email validation - consider using a proper email validator
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto, Long updatedBy) {
        log.info("Updating user with id: {}", id);
        
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        // Check if email is being updated and is already in use
        if (!existingUser.getEmail().equalsIgnoreCase(userDto.getEmail()) && 
                userRepository.existsByEmailAndIdNot(userDto.getEmail(), id)) {
            throw new BadRequestException(EMAIL_IN_USE);
        }
        
        // Check if username is being updated and is already in use
        if (!existingUser.getUsername().equalsIgnoreCase(userDto.getUsername()) && 
                userRepository.existsByUsernameAndIdNot(userDto.getUsername(), id)) {
            throw new BadRequestException(USERNAME_IN_USE);
        }
        
        // Update user fields
        existingUser.setUsername(userDto.getUsername());
        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setLastName(userDto.getLastName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setPhone(userDto.getPhone());
        existingUser.setAddress(userDto.getAddress());
        existingUser.setProfileImage(userDto.getProfileImage());
        
        // Update role if provided
        if (userDto.getRole() != null) {
            setUserRole(existingUser, userDto.getRole());
        }
        
        // Update password if provided
        if (StringUtils.hasText(userDto.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        
        // Update audit fields
        existingUser.setUpdatedBy(updatedBy);
        
        User updatedUser = userRepository.save(existingUser);
        log.info("Updated user with id: {}", id);
        
        return mapToDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        log.debug("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToDto(user);
    }
    
    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("No authenticated user found");
        }
        
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        log.debug("Fetching all users with page: {}, size: {}", 
                pageable.getPageNumber(), pageable.getPageSize());
                
        // Apply default sorting if not specified
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, DEFAULT_SORT_FIELD)
            );
        }
        
        return userRepository.findAll(pageable)
                .map(this::mapToDto);
    }
    
    @Override
    public List<User> findByRole(SystemRole role) {
        log.debug("Fetching users with role: {}", role);
        return userRepository.findByRoleName(role);
    }

    @Override
    @Transactional
    public void deleteUser(Long id, Long deletedBy) {
        log.info("Deleting user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        // Soft delete by setting active to false
        user.setActive(false);
        user.setUpdatedBy(deletedBy);
        
        // Invalidate any active sessions/tokens
        user.setLastPasswordResetDate(Instant.now());
        
        userRepository.save(user);
        log.info("User with id: {} has been deactivated", id);
    }

    @Override
    @Transactional
    public UserDto activateUser(Long id, boolean active, Long updatedBy) {
        log.info("Setting active status to {} for user id: {}", active, id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        // If activating, ensure the user has a verified email if required
        if (active && requireEmailVerification && !user.isEmailVerified()) {
            throw new BadRequestException("Cannot activate user with unverified email");
        }
        
        user.setActive(active);
        user.setUpdatedBy(updatedBy);
        
        // If deactivating, invalidate any active sessions/tokens
        if (!active) {
            user.setLastPasswordResetDate(Instant.now());
        }
        
        User updatedUser = userRepository.save(user);
        
        return mapToDto(updatedUser);
    }

    @Override
    public JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest) {
        log.debug("Authenticating user: {}", loginRequest.getUsernameOrEmail());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Get the authenticated user
            User user = (User) authentication.getPrincipal();
            
            // Check if account is active
            if (!user.isActive()) {
                throw new BadCredentialsException(ACCOUNT_DISABLED);
            }
            
            // Check if email verification is required
            if (requireEmailVerification && !user.isEmailVerified()) {
                throw new BadCredentialsException(EMAIL_VERIFICATION_REQUIRED);
            }
            
            // Generate JWT token
            String jwt = tokenProvider.generateToken(authentication);
            
            // Update last login timestamp
            user.setLastLogin(Instant.now());
            userRepository.save(user);
            
            // Build response
            return JwtAuthenticationResponse.builder()
                    .accessToken(jwt)
                    .tokenType("Bearer")
                    .expiresIn(jwtExpirationInMs / 1000) // Convert to seconds
                    .user(mapToDto(user))
                    .build();
                    
        } catch (BadCredentialsException ex) {
            log.warn("Authentication failed for user: {}", loginRequest.getUsernameOrEmail());
            throw new UnauthorizedException(INVALID_CREDENTIALS);
        }
    }

    @Override
    public String generateRandomPassword() {
        log.debug("Generating random password");
        
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "!@#$%^&*_=+-";
        String combined = upperCase + lowerCase + numbers + specialChars;
        
        Random random = new Random();
        StringBuilder password = new StringBuilder(12);
        
        // Asegurar al menos un carácter de cada conjunto
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));
        
        // Completar el resto
        for (int i = 4; i < 12; i++) {
            password.append(combined.charAt(random.nextInt(combined.length())));
        }
        
        // Mezclar la contraseña para hacerla más aleatoria
        char[] passwordArray = password.toString().toCharArray();
        for (int i = 0; i < passwordArray.length; i++) {
            int randomIndex = random.nextInt(passwordArray.length);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[randomIndex];
            passwordArray[randomIndex] = temp;
        }
        
        return new String(passwordArray);
    }

    @Override
    public void sendPasswordEmail(User user, String plainPassword) {
        try {
            emailService.sendPasswordResetEmail(user, plainPassword);
        } catch (Exception e) {
            log.error("Failed to send password reset email to user: {}", user.getEmail(), e);
            throw new RuntimeException("Failed to send password reset email");
        }
    }
    
    @Override
    @Transactional
    public UserDto updateUserRole(Long userId, RoleName roleName, Long updatedBy) {
        log.info("Updating role to {} for user id: {}", roleName, userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
                
        // Get the role entity
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName.name()));
        
        // Update the role
        user.setRole(role);
        user.setUpdatedBy(updatedBy);
        
        User updatedUser = userRepository.save(user);
        log.info("Updated role for user id: {} to {}", userId, roleName);
        
        return mapToDto(updatedUser);
    }
    
    @Override
    public Set<RoleDto> getUserRoles(Long userId) {
        log.debug("Fetching roles for user id: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // In this implementation, a user has one role, but we return it as a Set for consistency
        // with systems that support multiple roles per user
        return Set.of(roleMapper.toDto(user.getRole()));
    }
    
    @Override
    public boolean hasRole(Long userId, RoleName roleName) {
        log.debug("Checking if user {} has role: {}", userId, roleName);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
                
        return user.getRole() != null && user.getRole().getName() == roleName;
    }
    
    @Override
    @Transactional
    public void resetPassword(Long userId, String newPassword, Long updatedBy) {
        log.info("Resetting password for user id: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedBy(updatedBy);
        user.setLastPasswordResetDate(Instant.now());
        
        userRepository.save(user);
        log.info("Password reset for user id: {}", userId);
        
        // Invalidate any active sessions/tokens
        // This is handled by checking lastPasswordResetDate in the JWT validation
    }
    
    @Override
    @Transactional
    public boolean requestPasswordReset(String email) {
        log.info("Processing password reset request for email: {}", email);
        
        return userRepository.findByEmail(email)
                .map(user -> {
                    // Generate a reset token
                    String resetToken = UUID.randomUUID().toString();
                    user.setPasswordResetToken(resetToken);
                    user.setPasswordResetTokenExpiry(Instant.now().plusSeconds(24 * 60 * 60)); // 24 hours
                    
                    userRepository.save(user);
                    
                    // Send email with reset link
                    try {
                        emailService.sendPasswordResetEmail(user, resetToken);
                        return true;
                    } catch (Exception e) {
                        log.error("Failed to send password reset email to: {}", email, e);
                        return false;
                    }
                })
                .orElse(false); // Return false if user not found
    }
    
    @Override
    @Transactional
    public boolean verifyEmail(String token) {
        log.debug("Verifying email with token: {}", token);
        
        return userRepository.findByVerificationToken(token)
                .map(user -> {
                    if (user.isEmailVerified()) {
                        log.debug("Email already verified for user: {}", user.getEmail());
                        return true;
                    }
                    
                    user.setEmailVerified(true);
                    user.setVerificationToken(null);
                    userRepository.save(user);
                    
                    log.info("Email verified for user: {}", user.getEmail());
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<User> findByRole(SystemRole role) {
        return userRepository.findByRole(role);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }
    
    private UserDto mapToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
}
