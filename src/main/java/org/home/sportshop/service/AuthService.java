package org.home.sportshop.service;

import java.util.List;
import java.util.stream.Collectors;

import org.home.sportshop.model.Role;
import org.home.sportshop.model.User;
import org.home.sportshop.model.dto.AuthRequest;
import org.home.sportshop.model.dto.AuthResponse;
import org.home.sportshop.model.dto.CheckAuthResponse;
import org.home.sportshop.model.dto.UserResponse;
import org.home.sportshop.repository.RoleRepository;
import org.home.sportshop.repository.UserRepository;
import org.home.sportshop.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(UserRepository userRepository, RoleRepository roleRepository, 
                        PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public UserResponse registerUser(AuthRequest request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create a new user with encoded password
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());

        // Assign USER role by default
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Error: Role USER not found"));
        user.addRole(userRole);

        User savedUser = userRepository.save(user);

        return new UserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user);
        
        // Convert roles to string list
        List<String> roles = user.getRoles().stream()
                                  .map(Role::getName)
                                  .collect(Collectors.toList());
        
        AuthResponse.UserDto userDto = new AuthResponse.UserDto(user.getId(), user.getUsername(), roles);
        return new AuthResponse(token, userDto);
    }

    public CheckAuthResponse checkAuth(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return new CheckAuthResponse(false, null);
        }

        String jwtToken = token.substring(7);
        try {
            String username = jwtUtil.extractUsername(jwtToken);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Convert roles to string list
            List<String> roles = user.getRoles().stream()
                                      .map(Role::getName)
                                      .collect(Collectors.toList());
            
            AuthResponse.UserDto userDto = new AuthResponse.UserDto(user.getId(), user.getUsername(), roles);
            return new CheckAuthResponse(true, userDto);
        } catch (Exception e) {
            return new CheckAuthResponse(false, null);
        }
    }

    public User getUserByToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }

        String jwtToken = token.substring(7);
        try {
            String username = jwtUtil.extractUsername(jwtToken);
            return userRepository.findByUsername(username).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
} 