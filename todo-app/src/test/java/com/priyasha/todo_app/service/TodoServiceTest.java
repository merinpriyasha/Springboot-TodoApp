package com.priyasha.todo_app.service;

import com.priyasha.todo_app.dto.TodoDTO;
import com.priyasha.todo_app.model.Todo;
import com.priyasha.todo_app.model.User;
import com.priyasha.todo_app.repository.TodoRepository;
import com.priyasha.todo_app.repository.UserRepository;
import com.priyasha.todo_app.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TodoService todoService;


    @Test
    public void testCreateTodo(){

        String token = "valid-jwt-token";
        Long userId = 123L;

        // Define the input DTO and expected entity
        TodoDTO inputTodoDTO = new TodoDTO(null, "New Task", "pending", LocalDate.now().plusDays(1), "P1");

        // Create a User instance with the userId
        User user = new User();
        user.setId(userId);

        // Mock the methods used within the createTodo method
        when(jwtUtil.extractUserId(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Mock the mapping of DTO to entity and back to DTO
        Todo todo = new Todo(null, "New Task", "pending", LocalDate.now().plusDays(1), "P1", user);
        when(modelMapper.map(inputTodoDTO, Todo.class)).thenReturn(todo);

        // Mock the save operation on repository and the mapping back to DTO
        Todo savedTodo = new Todo(1L, "New Task", "pending", LocalDate.now().plusDays(1), "P1", user);
        when(todoRepository.save(todo)).thenReturn(savedTodo);
        when(modelMapper.map(savedTodo, TodoDTO.class)).thenReturn(new TodoDTO(1L, "New Task", "pending", LocalDate.now().plusDays(1), "P1"));

        // Execute the method and assert results
        TodoDTO result = todoService.createTodo(inputTodoDTO, token);

        // Assertions to verify the creation
        assertNotNull(result);
        assertEquals("New Task", result.getTask());
        assertEquals("pending", result.getStatus());
        assertEquals("P1", result.getPriority());
        assertEquals(1L, result.getId());

    }

    @Test
    public void testUpdateTodo(){

        Long id = 1L;
        String token = "valid-jwt-token";
        Long userId = 123L;

        // Prepare the TodoDTO input and the existing Todo entity
        TodoDTO todoDTO = new TodoDTO(null, "Updated Task", "completed", LocalDate.now().plusDays(2), "P2");

        // Create a User instance and set the userId
        User mockUser = new User();
        mockUser.setId(userId);

        // Prepare the existing Todo entity with the user
        Todo existingTodo = new Todo();
        existingTodo.setUser(mockUser);

        // Mock the JWT user extraction and repository find/save operations
        when(jwtUtil.extractUserId(token)).thenReturn(userId);
        when(todoRepository.findById(id)).thenReturn(Optional.of(existingTodo));

        // Update the existingTodo values as per the update
        existingTodo.setTask(todoDTO.getTask());
        existingTodo.setStatus(todoDTO.getStatus());
        existingTodo.setPriority(todoDTO.getPriority());
        existingTodo.setDeadlineDate(todoDTO.getDeadlineDate());

        when(todoRepository.save(existingTodo)).thenReturn(existingTodo);
        when(modelMapper.map(existingTodo, TodoDTO.class)).thenReturn(todoDTO);

        // Run the update method
        TodoDTO result = todoService.updateTodo(id, todoDTO, token);

        // Assertions to verify the updated values
        assertNotNull(result);
        assertEquals("Updated Task", result.getTask());
        assertEquals("completed", result.getStatus());
        assertEquals("P2", result.getPriority());
        assertEquals(todoDTO.getDeadlineDate(), result.getDeadlineDate());

    }

    @Test
    public void testGetTodos() {

        String token = "valid-jwt-token";
        Long userId = 123L;
        String keyword = "Task"; // Example keyword
        String status = "pending"; // Example status
        String sortBy = "task"; // Field to sort by
        String sortDirection = "asc"; // Sorting direction
        int page = 0; // Page number
        int size = 2; // Page size

        // Create Pageable object
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));

        // Mock the User object
        User mockUser = Mockito.mock(User.class);
        when(mockUser.getId()).thenReturn(userId);

        // Create Todo entities
        Todo todo1 = new Todo(1L, "Task 1", "pending", LocalDate.now().plusDays(1), "P1", mockUser);
        Todo todo2 = new Todo(2L, "Task 2", "pending", LocalDate.now().plusDays(2), "P2", mockUser);
        List<Todo> todos = Arrays.asList(todo1, todo2);

        // Mock the JWT user extraction and repository find operation
        when(jwtUtil.extractUserId(token)).thenReturn(userId);
        when(todoRepository.findByUserIdAndTaskContainingIgnoreCase(userId, keyword, pageable)).thenReturn(new PageImpl<>(todos));

        // Execute the getTodos method
        Page<TodoDTO> result = todoService.getTodos(keyword, status, sortBy, sortDirection, page, size, token);

        // Verify that the result contains the expected number of todos
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Task 1", result.getContent().get(0).getTask());
        assertEquals("Task 2", result.getContent().get(1).getTask());

    }

    @Test
    public void testDeleteTodo() {

        Long id = 1L;
        String token = "valid-jwt-token";
        Long userId = 123L;

        // Mocking the User object
        User mockUser = Mockito.mock(User.class);
        when(mockUser.getId()).thenReturn(userId);

        Todo existingTodo = new Todo();
        existingTodo.setId(id);
        existingTodo.setUser(mockUser);

        // Mock dependencies
        when(jwtUtil.extractUserId(token)).thenReturn(userId);
        when(todoRepository.findById(id)).thenReturn(Optional.of(existingTodo));

        // Execute the delete method
        todoService.deleteTodo(id, token);

        // Verify that deleteById was called with the correct ID
        verify(todoRepository).deleteById(id);

    }

}
