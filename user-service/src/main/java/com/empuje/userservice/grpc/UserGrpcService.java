package com.empuje.userservice.grpc;

import com.empuje.userservice.dto.UserDto;
import com.empuje.userservice.exception.ResourceNotFoundException;
import com.empuje.userservice.model.RoleName;
import com.empuje.userservice.service.UserService;
import com.empuje.userservice.grpc.gen.*;
import com.empuje.userservice.security.JwtTokenProvider;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.empuje.userservice.grpc.gen.UserServiceGrpc.UserServiceImplBase;

/**
 * gRPC service implementation for user management
 */
/**
 * gRPC service implementation for user management.
 */
@Slf4j
@GrpcService
@Service
@RequiredArgsConstructor
public class UserGrpcService extends UserServiceImplBase {

    private static final String TOKEN_TYPE = "Bearer";
    private static final long TOKEN_EXPIRATION = 86400000; // 24 hours in milliseconds
    private static final long SYSTEM_USER_ID = 1L; // System user ID for audit fields

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(com.empuje.userservice.grpc.gen.CreateUserRequest request,
                         io.grpc.stub.StreamObserver<com.empuje.userservice.grpc.gen.UserResponse> responseObserver) {
        try {
            // Validate request
            if (request.getUsername().isEmpty() || request.getEmail().isEmpty() || request.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Username, email and password are required");
            }

            // Map request to DTO
            UserDto userDto = new UserDto();
            userDto.setUsername(request.getUsername());
            userDto.setEmail(request.getEmail());
            userDto.setPassword(passwordEncoder.encode(request.getPassword()));
            userDto.setFirstName(request.getFirstName());
            userDto.setLastName(request.getLastName());
            userDto.setPhone(request.getPhone());
            userDto.setAddress(request.getAddress());
            
            // Set default role if not provided
            String roleName = request.getRole().isEmpty() ? "ROLE_DONANTE" : request.getRole();
            userDto.setRole(RoleName.valueOf(roleName));
            
            // Create user
            UserDto createdUser = userService.createUser(userDto, SYSTEM_USER_ID);
            
            // Build and send response
            responseObserver.onNext(toUserResponse(createdUser));
            responseObserver.onCompleted();
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid create user request: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .asRuntimeException());
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Error creating user: " + e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            // Map request to DTO
            UserDto userDto = new UserDto();
            
            // Update fields if provided
            if (request.hasUsername()) userDto.setUsername(request.getUsername());
            if (request.hasEmail()) userDto.setEmail(request.getEmail());
            if (request.hasFirstName()) userDto.setFirstName(request.getFirstName());
            if (request.hasLastName()) userDto.setLastName(request.getLastName());
            if (request.hasPhone()) userDto.setPhone(request.getPhone());
            if (request.hasAddress()) userDto.setAddress(request.getAddress());
            
            // Update role if provided
            if (request.hasRole()) {
                userDto.setRole(RoleName.valueOf(request.getRole()));
            }
            
            // Update password if provided
            if (request.hasPassword()) {
                userDto.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            
            // Update is_active if provided
            if (request.hasIsActive()) {
                userDto.setActive(request.getIsActive());
            }
            
            // Update user
            UserDto updatedUser = userService.updateUser(request.getId(), userDto, SYSTEM_USER_ID);
            
            // Build and send response
            responseObserver.onNext(toUserResponse(updatedUser));
            responseObserver.onCompleted();
            
        } catch (ResourceNotFoundException e) {
            log.warn("User not found: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid update request: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .asRuntimeException());
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Error updating user: " + e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void getUser(GetUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            if (request.getId() <= 0) {
                throw new IllegalArgumentException("Invalid user ID");
            }
            
            Optional<UserDto> userDtoOpt = userService.getUserById(request.getId());
            if (userDtoOpt.isEmpty()) {
                throw new ResourceNotFoundException("User not found with id: " + request.getId());
            }
            
            responseObserver.onNext(toUserResponse(userDtoOpt.get()));
            responseObserver.onCompleted();
            
        } catch (ResourceNotFoundException e) {
            log.warn("User not found: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .asRuntimeException());
        } catch (Exception e) {
            log.error("Error getting user: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Error getting user: " + e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void listUsers(ListUsersRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            // Convert page to 0-based for Spring Data
            int page = Math.max(0, request.getPage());
            int size = request.getSize() > 0 ? request.getSize() : 10;
            
            // Get users with pagination
            List<UserDto> users = userService.getAllUsers(page, size);
            
            // Stream responses
            users.stream()
                .map(this::toUserResponse)
                .forEach(responseObserver::onNext);
            
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("Error listing users: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Error listing users: " + e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        try {
            // Validate request
            if (!request.hasUsername() || !request.hasPassword() || 
                request.getUsername().isEmpty() || request.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Username and password are required");
            }
            
            // Create login request DTO
            UserDto loginRequest = new UserDto();
            loginRequest.setUsername(request.getUsername());
            loginRequest.setPassword(request.getPassword());
            
            // Authenticate user
            UserDto userDto = userService.authenticateUser(loginRequest)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
            
            // Generate JWT token
            String token = jwtTokenProvider.generateToken(userDto);
            
            // Build response
            UserResponse userResponse = toUserResponse(userDto);
            
            LoginResponse response = LoginResponse.newBuilder()
                .setToken(token)
                .setUser(userResponse)
                .setTokenType(TOKEN_TYPE)
                .setExpiresIn(TOKEN_EXPIRATION / 1000) // Convert to seconds
                .build();
                
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed: {}", e.getMessage());
            responseObserver.onError(Status.UNAUTHENTICATED
                .withDescription("Invalid username or password")
                .asRuntimeException());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid login request: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .asRuntimeException());
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("An error occurred during login")
                .asRuntimeException());
        }
    }

    @Override
    public void checkUsername(com.empuje.userservice.grpc.gen.CheckUsernameRequest request,
                            io.grpc.stub.StreamObserver<com.empuje.userservice.grpc.gen.CheckUsernameResponse> responseObserver) {
        try {
            // Validate request
            if (request.getUsername().isEmpty()) {
                throw new IllegalArgumentException("Username is required");
            }
            
            // Check username availability
            boolean available = !userService.existsByUsername(request.getUsername());
            
            // Build response
            com.empuje.userservice.grpc.gen.CheckUsernameResponse response = 
                com.empuje.userservice.grpc.gen.CheckUsernameResponse.newBuilder()
                    .setAvailable(available)
                    .build();
                    
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid check username request: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .asRuntimeException());
        } catch (Exception e) {
            log.error("Error checking username: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Error checking username: " + e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void checkEmail(com.empuje.userservice.grpc.gen.CheckEmailRequest request,
                         io.grpc.stub.StreamObserver<com.empuje.userservice.grpc.gen.CheckEmailResponse> responseObserver) {
        try {
            // Validate request
            if (request.getEmail().isEmpty()) {
                throw new IllegalArgumentException("Email is required");
            }
            
            // Check email availability
            boolean available = !userService.existsByEmail(request.getEmail());
            
            // Build response
            com.empuje.userservice.grpc.gen.CheckEmailResponse response = 
                com.empuje.userservice.grpc.gen.CheckEmailResponse.newBuilder()
                    .setAvailable(available)
                    .build();
                    
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid check email request: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .asRuntimeException());
        } catch (Exception e) {
            log.error("Error checking email: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Error checking email: " + e.getMessage())
                .asRuntimeException());
        }
    }
    
    @Override
    public void deleteUser(com.empuje.userservice.grpc.gen.DeleteUserRequest request,
                         io.grpc.stub.StreamObserver<com.empuje.userservice.grpc.gen.DeleteUserResponse> responseObserver) {
        try {
            // Validate request
            if (request.getId() <= 0) {
                throw new IllegalArgumentException("Invalid user ID");
            }
            
            // Delete user
            userService.deleteUser(request.getId(), SYSTEM_USER_ID);
            
            // Build and send response
            responseObserver.onNext(com.empuje.userservice.grpc.gen.DeleteUserResponse.newBuilder().build());
            responseObserver.onCompleted();
            
        } catch (ResourceNotFoundException e) {
            log.warn("User not found for deletion: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid delete request: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .asRuntimeException());
        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Error deleting user: " + e.getMessage())
                .asRuntimeException());
        }
    }
}
