package com.priyasha.todo_app.controller;

import com.priyasha.todo_app.dto.UserDTO;
import com.priyasha.todo_app.service.AuthService;
import com.priyasha.todo_app.service.TodoService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("${auth.base-url}")
public class AuthController {

    @Autowired
    private AuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(TodoService.class); // Create logger instance

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody UserDTO userDTO) {

        // Call the authService to perform registration and get the response
        ResponseEntity<Map<String, Object>> response = authService.register(userDTO);
        return response; // Return the response from authServicerDTO));

    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody UserDTO userDTO, HttpServletResponse response) {

        logger.info("User login attempt with email: {}", userDTO.getEmail());

        // Call the authService to perform the login logic
        ResponseEntity<Map<String, Object>> responseMap = authService.login(userDTO, response); // Update to get the response map
        // Return the response map with an OK status
        return responseMap;

    }
}
