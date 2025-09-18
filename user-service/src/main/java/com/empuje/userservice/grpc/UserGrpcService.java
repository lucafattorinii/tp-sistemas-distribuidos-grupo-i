// Deprecated reference file. The active gRPC implementation is UserGrpcServiceImpl.
// This file is intentionally left minimal to avoid IDE parsing errors.
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final ProtoMapper protoMapper;

    @Override
    public void createUser(com.empuje.userservice.grpc.gen.CreateUserRequest request,
                         io.grpc.stub.StreamObserver<com.empuje.userservice.grpc.gen.CreateUserResponse> responseObserver) {
        try {
            // Validate request
            if (request.getUsername() == null || request.getUsername().isEmpty() || 
                request.getEmail() == null || request.getEmail().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Username, email and password are required");
            }
*/

            // Map request to DTO
            UserDto userDto = new UserDto();
            userDto.setUsername(request.getUsername());
            userDto.setEmail(request.getEmail());
            userDto.setPassword(passwordEncoder.encode(request.getPassword()));
            
            // Set optional fields
            if (request.getFirstName() != null) userDto.setFirstName(request.getFirstName());
            if (request.getLastName() != null) userDto.setLastName(request.getLastName());
            if (request.getPhone() != null) userDto.setPhone(request.getPhone());
            if (request.getAddress() != null) userDto.setAddress(request.getAddress());
            if (request.getProfileImage() != null) userDto.setProfileImage(request.getProfileImage());
            
            // Set role
            // Set role if provided
            if (!request.getRole().isEmpty()) {
                userDto.setRole(SystemRole.valueOf(request.getRole()));
            } else {
                // Default role
                userDto.setRole(SystemRole.ROLE_DONANTE);
            }
            
            // Create user
            UserDto createdUser = userService.createUser(userDto, SYSTEM_USER_ID);
            
            // Build response
            com.empuje.userservice.grpc.gen.CreateUserResponse response = 
                com.empuje.userservice.grpc.gen.CreateUserResponse.newBuilder()
                    .setId(createdUser.getId())
                    .build();
            
            // Send response
            responseObserver.onNext(response);
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
    public void updateUser(com.empuje.userservice.grpc.gen.UpdateUserRequest request,
                         io.grpc.stub.StreamObserver<com.empuje.userservice.grpc.gen.UserResponse> responseObserver) {
        try {
            // Map request to DTO using ProtoMapper
            UserDto userDto = protoMapper.toUserDto(request);
            
            // Update password if provided
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                userDto.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            
            // Update user
            UserDto updatedUser = userService.updateUser(request.getId(), userDto, SYSTEM_USER_ID);
            
            // Build and send response
            responseObserver.onNext(protoMapper.toUserResponse(updatedUser));
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
    public void getUser(com.empuje.userservice.grpc.gen.GetUserRequest request, 
                       io.grpc.stub.StreamObserver<com.empuje.userservice.grpc.gen.UserResponse> responseObserver) {
        try {
            if (request.getId() <= 0) {
                throw new IllegalArgumentException("Invalid user ID");
            }
            
            try {
                UserDto userDto = userService.getUserById(request.getId());
                responseObserver.onNext(toUserResponse(userDto));
            } catch (ResourceNotFoundException e) {
                throw new ResourceNotFoundException("User not found with id: " + request.getId());
            }
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
    public void listUsers(com.empuje.userservice.grpc.gen.ListUsersRequest request, 
                         io.grpc.stub.StreamObserver<com.empuje.userservice.grpc.gen.UserResponse> responseObserver) {
        try {
            // Create Pageable for pagination (convert to 0-based page for Spring Data)
            int page = Math.max(0, request.getPage() - 1);
            int size = request.getSize() > 0 ? request.getSize() : 10;
            org.springframework.data.domain.Pageable pageable = 
                org.springframework.data.domain.PageRequest.of(page, size);
            
            // Get users with pagination
            org.springframework.data.domain.Page<UserDto> usersPage = userService.getAllUsers(pageable);
            
            // Stream responses
            usersPage.getContent().stream()
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
    public void login(com.empuje.userservice.grpc.gen.LoginRequest request, 
                     io.grpc.stub.StreamObserver<com.empuje.userservice.grpc.gen.LoginResponse> responseObserver) {
        try {
            // Validate request
            if (request.getUsername().isEmpty() || request.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Username and password are required");
            }
            
            // Create LoginRequest for authentication
            com.empuje.userservice.dto.LoginRequest loginRequest = 
                new com.empuje.userservice.dto.LoginRequest();
            loginRequest.setUsernameOrEmail(request.getUsername());
            loginRequest.setPassword(request.getPassword());
            
            // Authenticate user using the service
            com.empuje.userservice.dto.JwtAuthenticationResponse authResponse = 
                userService.authenticateUser(loginRequest);
            
            // Extract user details from auth response
            UserDto userDto = authResponse.getUser();
            String role = userDto.getRole() != null ? userDto.getRole().name() : "USER";
            
            // Create user details for token generation
            org.springframework.security.core.userdetails.User userDetails = 
                new org.springframework.security.core.userdetails.User(
                    userDto.getUsername(),
                    userDto.getPassword(),
                    true, // enabled
                    true, // accountNonExpired
                    true, // credentialsNonExpired
                    true, // accountNonLocked
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );
            
            // Generate JWT token
            String token = jwtTokenProvider.generateToken(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
                )
            );
            
            // Build response
            com.empuje.userservice.grpc.gen.UserResponse userResponse = toUserResponse(userDto);
            
            com.empuje.userservice.grpc.gen.LoginResponse response = 
                com.empuje.userservice.grpc.gen.LoginResponse.newBuilder()
                    .setToken(token)
                    .setUser(userResponse)
                    .setExpiresIn(TOKEN_EXPIRATION / 1000) // Convert to seconds
                    .setTokenType(TOKEN_TYPE)
                    .build();
                    
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed: {}", e.getMessage());
            responseObserver.onError(Status.UNAUTHENTICATED
                .withDescription("Invalid username or password")
                .asRuntimeException());
        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Error during login: " + e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void checkUsername(com.empuje.userservice.grpc.gen.CheckUsernameRequest request,
                            io.grpc.stub.StreamObserver<com.empuje.userservice.grpc.gen.CheckAvailabilityResponse> responseObserver) {
        try {
            // Validate request
            if (request.getUsername().isEmpty()) {
                throw new IllegalArgumentException("Username is required");
            }
            
            // Check username availability
            boolean available = !userService.existsByUsername(request.getUsername());
            
            // Build response
            com.empuje.userservice.grpc.gen.CheckAvailabilityResponse response = com.empuje.userservice.grpc.gen.CheckAvailabilityResponse.newBuilder()
                .setAvailable(available)
                .setMessage(available ? "Username is available" : "Username is already taken")
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
            com.empuje.userservice.grpc.gen.CheckEmailResponse response = com.empuje.userservice.grpc.gen.CheckEmailResponse.newBuilder()
                .setAvailable(available)
                .setMessage(available ? "Email is available" : "Email is already registered")
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
                         io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
        try {
            // Validate request
            if (request.getId() <= 0) {
                throw new IllegalArgumentException("Invalid user ID");
            }
            
            // Delete user
            userService.deleteUser(request.getId(), SYSTEM_USER_ID);
            
            // Send empty response as defined in the proto
            responseObserver.onNext(com.google.protobuf.Empty.newBuilder().build());
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
    
    /**
     * Converts a UserDto to a UserResponse protobuf message
     */
    private com.empuje.userservice.grpc.gen.UserResponse toUserResponse(UserDto userDto) {
        return protoMapper.toUserResponse(userDto);
    }
}
