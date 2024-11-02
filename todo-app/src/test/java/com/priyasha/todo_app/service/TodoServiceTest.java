package com.priyasha.todo_app.service;

import com.priyasha.todo_app.dto.TodoDTO;
import com.priyasha.todo_app.model.Todo;
import com.priyasha.todo_app.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

    @InjectMocks
    private TodoService todoService;

    @Test
    public void testCreateTodo(){
        TodoDTO todoDTO = new TodoDTO(1L, "New Task", "pending", LocalDate.now().plusDays(1), "P1");
        Todo todo = new Todo();

        // When the ModelMapper and repository save are mocked to simulate expected behavior
        when(modelMapper.map(todoDTO, Todo.class)).thenReturn(todo);
        when(todoRepository.save(todo)).thenReturn(todo);
        when(modelMapper.map(todo, TodoDTO.class)).thenReturn(todoDTO);

        // When the service method is called
        TodoDTO result = todoService.createTodo(todoDTO);

        assertEquals("New Task", result.getTask(), "Task should be match the input value");
        assertEquals("pending", result.getStatus(), "Status should be 'pending' ");
        assertEquals(LocalDate.now().plusDays(1), result.getDeadlineDate(), "Deadline date should match the input value");
        assertEquals("P1", result.getPriority(), "Priority should be 'P1'");

    }

    @Test
    public void testUpdateTodo(){
        Long id = 1L;
        TodoDTO todoDTO = new TodoDTO(null,"Updated Task", "completed", LocalDate.now().plusDays(2), "P2");
        Todo todo= new Todo();

        Optional<Todo> optionalTodo = Optional.of(todo);

        when(todoRepository.findById(id)).thenReturn(optionalTodo);
        when(todoRepository.save(todo)).thenReturn(todo);
        when(modelMapper.map(todo, TodoDTO.class)).thenReturn(todoDTO);

        TodoDTO result = todoService.updateTodo(id, todoDTO);

        assertEquals("Updated Task", result.getTask());
        assertEquals("completed", result.getStatus());
        assertEquals(LocalDate.now().plusDays(2), result.getDeadlineDate());
        assertEquals("P2", result.getPriority());
    }

    @Test
    public void testGetTodos() {
        Todo todo1 = new Todo(1L, "Task 1", "pending", LocalDate.now().plusDays(1), "P1");
        Todo todo2 = new Todo(2L, "Task 2", "completed", LocalDate.now().plusDays(2), "P2");
        List<Todo> todos = Arrays.asList(todo1, todo2);
        // Provide a Pageable object and total elements count
        Page<Todo> page = new PageImpl<>(todos, PageRequest.of(0, 10), todos.size());

        when(todoRepository.findAll(any(Pageable.class))).thenReturn(page);

        // When
        Page<TodoDTO> result = todoService.getTodos(null, null, null, null, 0, 10);

        // Then
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(2, result.getTotalElements());

    }

    @Test
    public void testDeleteTodo() {
        Long id = 1L;
        when(todoRepository.existsById(id)).thenReturn(true);

        String response = todoService.deleteTodo(id);

        assertEquals("Todo with id 1 was successfully deleted.", response);
        verify(todoRepository, times(1)).deleteById(id);
    }

}
