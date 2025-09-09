package com.empuje.userservice.grpc;

import com.empuje.userservice.dto.UserDto;
import com.empuje.userservice.exception.ResourceNotFoundException;
import com.empuje.userservice.service.UserService;
import com.empuje.userservice.grpc.gen.*;
import com.empuje.userservice.security.JwtTokenProvider;
import com.empuje.userservice.util.ProtoMapper;
import com.empuje.userservice.grpc.gen.RoleName;
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
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@Service
@RequiredArgsConstructor
public class UserGrpcServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    
    private final UserService userService;
    private final ProtoMapper protoMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    
    // System user ID to use for createdBy/updatedBy fields
    private static final long SYSTEM_USER_ID = 1L;

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
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
                try {
                    RoleName role = RoleName.valueOf(request.getRole());
                    userDto.setRoleId(role.getNumber());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid role. Must be one of: " + 
                        Arrays.stream(RoleName.values())
                            .filter(r -> r != RoleName.UNRECOGNIZED)
                            .map(Enum::name)
                            .collect(Collectors.joining(", ")));
                }
            } else {
                // Default role if not provided
                userDto.setRoleId(RoleName.ROLE_DONANTE.getNumber());
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
                .setId(createdUser.getId())
                .setSuccess(true)
                .build();
                
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid create user request: {}", e.getMessage());
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
            // Get existing user or throw exception if not found
            UserDto existingUser = userService.getUserById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getId()));
            
            // Create DTO with updated fields
            UserDto userDto = new UserDto();
            
            // Only update fields that are provided in the request
            if (!request.getUsername().isEmpty()) userDto.setUsername(request.getUsername());
            if (!request.getEmail().isEmpty()) userDto.setEmail(request.getEmail());
            if (!request.getFirstName().isEmpty()) userDto.setFirstName(request.getFirstName());
            if (!request.getLastName().isEmpty()) userDto.setLastName(request.getLastName());
            if (!request.getPhone().isEmpty()) userDto.setPhone(request.getPhone());
            if (!request.getAddress().isEmpty()) userDto.setAddress(request.getAddress());
            
            // Update role if provided
            if (!request.getRole().isEmpty()) {
                try {
                    RoleName role = RoleName.valueOf(request.getRole());
                    userDto.setRoleId(role.getNumber());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid role. Must be one of: " + 
                        Arrays.stream(RoleName.values())
                            .filter(r -> r != RoleName.UNRECOGNIZED)
                            .map(Enum::name)
                            .collect(Collectors.joining(", ")));
                }
            }
            
            // Update password if provided
            if (!request.getPassword().isEmpty()) {
                userDto.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            
            // Update user
            UserDto updatedUser = userService.updateUser(request.getId(), userDto, SYSTEM_USER_ID);
            
            // Convert to response
            UserResponse response = UserResponse.newBuilder()
                .setId(updatedUser.getId())
                .setUsername(updatedUser.getUsername())
                .setEmail(updatedUser.getEmail())
                .setRole(RoleName.forNumber(updatedUser.getRoleId()).name())
                .setSuccess(true)
                .build();
            
            responseObserver.onNext(response);
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
            UserDto userDto = userService.getUserById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getId()));
            
            UserResponse response = UserResponse.newBuilder()
                .setId(userDto.getId())
                .setUsername(userDto.getUsername())
                .setEmail(userDto.getEmail())
                .setRole(RoleName.forNumber(userDto.getRoleId()).name())
                .setSuccess(true)
                .build();
                
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (ResourceNotFoundException e) {
            log.warn("User not found: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
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
            // Convert page and size to 0-based page for Spring Data
            int page = request.getPage() > 0 ? request.getPage() - 1 : 0;
            int size = request.getSize() > 0 ? request.getSize() : 10;
            
            // Get users with pagination
            List<UserDto> users = userService.getAllUsers(PageRequest.of(page, size));
            
            // Convert to response
            for (UserDto userDto : users) {
                UserResponse response = UserResponse.newBuilder()
                    .setId(userDto.getId())
                    .setUsername(userDto.getUsername())
                    .setEmail(userDto.getEmail())
                    .setRole(RoleName.forNumber(userDto.getRoleId()).name())
                    .setSuccess(true)
                    .build();
                    
                responseObserver.onNext(response);
            }
            
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
            if (request.getUsername().isEmpty() || request.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Username and password are required");
            }
            
            // Find user by username
            Optional<UserDto> userOpt = userService.findByUsername(request.getUsername());
            if (userOpt.isEmpty()) {
                throw new BadCredentialsException("Invalid username or password");
            }
            
            UserDto userDto = userOpt.get();
            
            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), userDto.getPassword())) {
                throw new BadCredentialsException("Invalid username or password");
            }
            
            // Generate JWT token
            String token = jwtTokenProvider.generateToken(userDto);
            
            // Build user response
            UserResponse userResponse = UserResponse.newBuilder()
                .setId(userDto.getId())
                .setUsername(userDto.getUsername())
                .setEmail(userDto.getEmail())
                .setRole(RoleName.forNumber(userDto.getRoleId()).name())
                .setSuccess(true)
                .build();
            
            // Build and send login response
            LoginResponse response = LoginResponse.newBuilder()
                .setToken(token)
                .setUser(userResponse)
                .build();
                
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for user: {}", request.getUsername());
            responseObserver.onError(Status.UNAUTHENTICATED
                .withDescription("Invalid username or password")
                .withCause(e)
                .asRuntimeException());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid login request: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .withCause(e)
                .asRuntimeException());
        } catch (Exception e) {
            log.error("Unexpected error during login for user {}: {}", 
                request.getUsername(), e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("An unexpected error occurred during login")
                .withCause(e)
                .asRuntimeException());
        }
    }

    @Override
    public void checkUsername(CheckUsernameRequest request, StreamObserver<CheckUsernameResponse> responseObserver) {
        try {
            boolean available = !userService.existsByUsername(request.getUsername());
            CheckUsernameResponse response = CheckUsernameResponse.newBuilder()
                .setAvailable(available)
                .build();
                
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("Error checking username: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Error checking username: " + e.getMessage())
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
            log.error("Error checking email: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Error checking email: " + e.getMessage())
                .asRuntimeException());
        }
    }
}
