package com.empuje.userservice.grpc;

import com.empuje.userservice.dto.JwtAuthenticationResponse;
import com.empuje.userservice.dto.UserDto;
import com.empuje.userservice.exception.ResourceNotFoundException;
import com.empuje.userservice.service.UserService;
import com.empuje.userservice.grpc.gen.*;
import com.empuje.userservice.security.JwtTokenProvider;
import com.empuje.userservice.util.ProtoMapper;
import com.empuje.userservice.grpc.gen.SystemRole;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Slf4j
@GrpcService
@Service
@RequiredArgsConstructor
public class UserGrpcServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    // System user ID to use for createdBy/updatedBy fields
    private static final long SYSTEM_USER_ID = 1L;

    @Override
    public void createUser(com.empuje.userservice.grpc.gen.CreateUserRequest request, 
                         StreamObserver<CreateUserResponse> responseObserver) {
        try {
            // Validate required fields
            if (request.getUsername().isEmpty() || request.getEmail().isEmpty() || request.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Username, email and password are required");
            }
            
            // Map request to DTO
            UserDto userDto = new UserDto();
            userDto.setUsername(request.getUsername());
            userDto.setEmail(request.getEmail());
            userDto.setPassword(passwordEncoder.encode(request.getPassword()));
            
            // Set role if provided
            if (!request.getRole().isEmpty()) {
                // Convert role string to enum value
                userDto.setRole(SystemRole.valueOf(request.getRole().toUpperCase()));
            } else {
                // Default role if not provided
                userDto.setRole(SystemRole.VOLUNTARIO);
            }
            
            // Set optional fields
            if (!request.getFirstName().isEmpty()) userDto.setFirstName(request.getFirstName());
            if (!request.getLastName().isEmpty()) userDto.setLastName(request.getLastName());
            if (!request.getPhone().isEmpty()) userDto.setPhone(request.getPhone());
            if (!request.getAddress().isEmpty()) userDto.setAddress(request.getAddress());
            
            // Create user
            UserDto createdUser = userService.createUser(userDto, SYSTEM_USER_ID);
            
            // Build response
            CreateUserResponse response = CreateUserResponse.newBuilder()
                .setId(createdUser.getId() != null ? createdUser.getId() : 0)
                .setUsername(createdUser.getUsername())
                .setEmail(createdUser.getEmail())
                .build();
                
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .asRuntimeException());
        } catch (Exception e) {
            log.error("Error creating user", e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Error creating user")
                .asRuntimeException());
        }
    }

    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            // Get existing user (this will throw ResourceNotFoundException if not found)
            UserDto existingUser = userService.getUserById(request.getId());
            
            // Create DTO with updated fields
            UserDto userDto = new UserDto();
            
            // Only update fields that are provided in the request
            if (!request.getUsername().isEmpty()) userDto.setUsername(request.getUsername());
            if (!request.getEmail().isEmpty()) userDto.setEmail(request.getEmail());
            if (!request.getFirstName().isEmpty()) userDto.setFirstName(request.getFirstName());
            if (!request.getLastName().isEmpty()) userDto.setLastName(request.getLastName());
            if (!request.getPhone().isEmpty()) userDto.setPhone(request.getPhone());
            if (!request.getAddress().isEmpty()) userDto.setAddress(request.getAddress());
            
            // Update role if provided and not empty
            if (!request.getRole().isEmpty()) {
                userDto.setRole(SystemRole.valueOf(request.getRole().toUpperCase()));
            }
            
            // Update password if provided
            if (!request.getPassword().isEmpty()) {
                userDto.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            
            // Update user
            UserDto updatedUser = userService.updateUser(request.getId(), userDto, SYSTEM_USER_ID);
            
            // Convert to response
            responseObserver.onNext(buildUserResponse(updatedUser));
            responseObserver.onCompleted();
            
        } catch (ResourceNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException());
        } catch (Exception e) {
            log.error("Error updating user", e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Error updating user")
                .asRuntimeException());
        }
    }

    @Override
    public void getUser(GetUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            UserDto userDto = userService.getUserById(request.getId());
            responseObserver.onNext(buildUserResponse(userDto));
            responseObserver.onCompleted();
            
        } catch (ResourceNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException());
        } catch (Exception e) {
            log.error("Error getting user", e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Error getting user")
                .asRuntimeException());
        }
    }

    @Override
    public void listUsers(ListUsersRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            // Convert page and size to 0-based page for Spring Data
            int page = request.getPage() > 0 ? request.getPage() - 1 : 0;
            int size = request.getSize() > 0 ? request.getSize() : 10;
            
            // Get users with pagination
            Page<UserDto> usersPage = userService.getAllUsers(PageRequest.of(page, size));
            
            // Convert to response
            for (UserDto userDto : usersPage.getContent()) {
                responseObserver.onNext(buildUserResponse(userDto));
            }
            
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("Error listing users", e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Error listing users")
                .asRuntimeException());
        }
    }

    /**
     * Helper method to build a UserResponse from UserDto
     */
    private UserResponse buildUserResponse(UserDto userDto) {
        UserResponse.Builder builder = UserResponse.newBuilder()
            .setId(userDto.getId())
            .setUsername(userDto.getUsername())
            .setEmail(userDto.getEmail());
            
        // Set role if available
        if (userDto.getRole() != null) {
            RoleResponse roleResponse = RoleResponse.newBuilder()
                .setId(userDto.getRole().getNumber())
                .setName(userDto.getRole().name())
                .setIsActive(true)
                .build();
            builder.setRole(roleResponse);
        } else {
            RoleResponse defaultRole = RoleResponse.newBuilder()
                .setId(SystemRole.ROLE_UNSPECIFIED.getNumber())
                .setName(SystemRole.ROLE_UNSPECIFIED.name())
                .setIsActive(true)
                .build();
            builder.setRole(defaultRole);
        }
        
        return builder.build();
    }

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        try {
            // Validate request
            if (request.getUsername().isEmpty() || request.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Username and password are required");
            }
            
            // Authenticate user and get JWT token
            com.empuje.userservice.dto.LoginRequest loginRequest = 
                new com.empuje.userservice.dto.LoginRequest();
            loginRequest.setUsernameOrEmail(request.getUsername());
            loginRequest.setPassword(request.getPassword());
            
            JwtAuthenticationResponse authResponse = userService.authenticateUser(loginRequest);
            UserDto userDto = authResponse.getUser();
            
            // Build and send login response
            LoginResponse response = LoginResponse.newBuilder()
                .setToken(authResponse.getAccessToken())
                .setUser(buildUserResponse(userDto))
                .build();
                
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (BadCredentialsException e) {
            responseObserver.onError(Status.UNAUTHENTICATED
                .withDescription("Invalid username or password")
                .asRuntimeException());
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .asRuntimeException());
        } catch (Exception e) {
            log.error("Login error", e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Login failed")
                .asRuntimeException());
        }
    }

    @Override
    public void checkUsername(CheckUsernameRequest request, StreamObserver<CheckAvailabilityResponse> responseObserver) {
        try {
            boolean available = !userService.existsByUsername(request.getUsername());
            CheckAvailabilityResponse response = CheckAvailabilityResponse.newBuilder()
                .setAvailable(available)
                .setMessage(available ? "Username is available" : "Username is already taken")
                .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error checking username availability", e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Error checking username availability")
                .asRuntimeException());
        }
    }

    @Override
    public void checkEmail(CheckEmailRequest request, StreamObserver<CheckEmailResponse> responseObserver) {
        try {
            boolean available = !userService.existsByEmail(request.getEmail());
            CheckEmailResponse response = CheckEmailResponse.newBuilder()
                .setAvailable(available)
                .build();
                
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("Error checking email", e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Error checking email")
                .asRuntimeException());
        }
    }
    
}
