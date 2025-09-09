package com.empuje.userservice.service.impl;

import com.empuje.userservice.dto.JwtAuthenticationResponse;
import com.empuje.userservice.dto.LoginRequest;
import com.empuje.userservice.dto.UserDto;
import com.empuje.userservice.exception.BadRequestException;
import com.empuje.userservice.exception.ResourceNotFoundException;
import com.empuje.userservice.model.Role;
import com.empuje.userservice.model.User;
import com.empuje.userservice.repository.RoleRepository;
import com.empuje.userservice.repository.UserRepository;
import com.empuje.userservice.security.JwtTokenProvider;
import com.empuje.userservice.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final EmailService emailService;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto, Long createdBy) {
        // Verificar si el nombre de usuario ya está en uso
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }

        // Verificar si el correo electrónico ya está en uso
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }

        // Crear nuevo usuario
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        
        // Establecer rol
        Role role = roleRepository.findByName(userDto.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", userDto.getRole().name()));
        user.setRole(role);
        
        // Generar y establecer contraseña
        String password = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(password));
        
        // Establecer creado por
        if (createdBy != null) {
            user.setCreatedBy(createdBy);
        }
        
        // Guardar usuario
        User savedUser = userRepository.save(user);
        
        // Enviar correo electrónico con credenciales
        try {
            emailService.sendUserRegistrationEmail(user, password);
        } catch (Exception e) {
            log.error("Error al enviar correo de registro al usuario: {}", user.getEmail(), e);
            // No fallar la petición si falla el envío del correo
        }
        
        return mapToDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto, Long updatedBy) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        // Verificar si se está cambiando el nombre de usuario y si ya está en uso
        if (!user.getUsername().equals(userDto.getUsername()) && 
            userRepository.existsByUsername(userDto.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }
        
        // Verificar si se está cambiando el correo electrónico y si ya está en uso
        if (!user.getEmail().equals(userDto.getEmail()) && 
            userRepository.existsByEmail(userDto.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }
        
        // Actualizar campos del usuario
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        
        // Actualizar rol si cambió
        if (user.getRole() == null || !user.getRole().getName().equals(userDto.getRole())) {
            Role role = roleRepository.findByName(userDto.getRole())
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", userDto.getRole().name()));
            user.setRole(role);
        }
        
        // Actualizar modificado por
        if (updatedBy != null) {
            user.setUpdatedBy(updatedBy);
        }
        
        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToDto(user);
    }

    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public void deleteUser(Long id, Long deletedBy) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        // Eliminación lógica estableciendo activo a falso
        user.setActive(false);
        user.setUpdatedBy(deletedBy);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDto activateUser(Long id, boolean active, Long updatedBy) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        user.setActive(active);
        user.setUpdatedBy(updatedBy);
        User updatedUser = userRepository.save(user);
        
        return mapToDto(updatedUser);
    }

    @Override
    public JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String jwt = tokenProvider.generateToken(authentication);
        User user = getCurrentUser();
        
        return JwtAuthenticationResponse.builder()
                .accessToken(jwt)
                .user(mapToDto(user))
                .build();
    }

    @Override
    public String generateRandomPassword() {
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "!@#$%^&*_=+-/";
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
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<User> findByRole(Role.RoleName roleName) {
        return userRepository.findByRoleName(roleName);
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
