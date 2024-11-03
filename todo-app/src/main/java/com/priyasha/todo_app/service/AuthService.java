package com.priyasha.todo_app.service;

import com.priyasha.todo_app.dto.UserDTO;
import com.priyasha.todo_app.model.User;
import com.priyasha.todo_app.repository.UserRepository;
import com.priyasha.todo_app.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);


    /**
     *
     * @param userRepository
     * @param passwordEncoder
     * @param jwtUtil
     * @param modelMapper
     */
    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.modelMapper = modelMapper;
    }

    public  ResponseEntity<Map<String, Object>> register(@Valid UserDTO userDTO){

        logger.info("Registering a user with email: {}", userDTO.getEmail());

        // Check if the email already exists in the database
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "Email already exists");
            responseMap.put("status", HttpStatus.CONFLICT.value()); // 409 Conflict status
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseMap);
        }
        try {
            User user = modelMapper.map(userDTO, User.class); // Map DTO to Entity
            user.setPassword(passwordEncoder.encode(user.getPassword())); // Encode password
            userRepository.save(user);

            // Create response map
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("status", HttpStatus.OK.value());
            responseMap.put("message", "Register successfully");
            responseMap.put("email", user.getEmail());

            return ResponseEntity.ok(responseMap);

        } catch (Exception e) {
            logger.error("User registration failed for email: {}", userDTO.getEmail(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User registration failed", e);
        }
    }
    public ResponseEntity<Map<String, Object>> login(@Valid UserDTO userDTO, HttpServletResponse response){

        logger.info("User login attempt with email: {}", userDTO.getEmail());

        User user = userRepository.findByEmail(userDTO.getEmail())
                .orElseThrow(() -> {
                    logger.warn("User with email {} not found", userDTO.getEmail());
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
                });

        if (passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            String token = jwtUtil.generateToken(user.getId(), user.getEmail());
            logger.info("Login successful for user: {}", user.getEmail());
            logger.info("Generated JWT Token: {}", token);
            // Create response map
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "Login successfully");
            responseMap.put("token", token);
            responseMap.put("status", HttpStatus.OK.value());

            return ResponseEntity.ok(responseMap);

        } else {
            // Log an error message and return a response indicating incorrect password
            logger.warn("Incorrect password provided for user: {}", userDTO.getEmail());
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "Incorrect password");
            responseMap.put("status", HttpStatus.UNAUTHORIZED.value());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
        }
    }
}
