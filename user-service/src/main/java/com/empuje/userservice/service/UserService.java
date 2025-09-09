package com.empuje.userservice.service;

import com.empuje.userservice.dto.JwtAuthenticationResponse;
import com.empuje.userservice.dto.LoginRequest;
import com.empuje.userservice.dto.UserDto;
import com.empuje.userservice.model.Role;
import com.empuje.userservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    
    UserDto createUser(UserDto userDto, Long createdBy);
    
    UserDto updateUser(Long id, UserDto userDto, Long updatedBy);
    
    UserDto getUserById(Long id);
    
    Page<UserDto> getAllUsers(Pageable pageable);
    
    void deleteUser(Long id, Long deletedBy);
    
    UserDto activateUser(Long id, boolean active, Long updatedBy);
    
    JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest);
    
    String generateRandomPassword();
    
    void sendPasswordEmail(User user, String plainPassword);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(Role.RoleName roleName);
    
    User getCurrentUser();
}
