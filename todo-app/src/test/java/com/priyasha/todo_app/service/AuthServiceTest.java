package com.priyasha.todo_app.service;

import com.priyasha.todo_app.dto.UserDTO;
import com.priyasha.todo_app.model.User;
import com.priyasha.todo_app.repository.UserRepository;
import com.priyasha.todo_app.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.http.ResponseEntity;


public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegister_Success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("Password123");

        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        User user = new User();
        user.setEmail(userDTO.getEmail());
        when(modelMapper.map(userDTO, User.class)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseEntity<Map<String, Object>> response = authService.register(userDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Register successfully", response.getBody().get("message"));
        assertEquals("test@example.com", response.getBody().get("email"));
    }

    @Test
    public void testLogin_Success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("Password123");
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user.getId(), user.getEmail())).thenReturn("mockedToken");

        ResponseEntity<Map<String, Object>> response = authService.login(userDTO, mock(HttpServletResponse.class));

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Login successfully", response.getBody().get("message"));
        assertEquals("mockedToken", response.getBody().get("token"));
    }
}
