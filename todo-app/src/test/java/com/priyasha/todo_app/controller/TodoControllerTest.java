package com.priyasha.todo_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.priyasha.todo_app.dto.TodoDTO;
import com.priyasha.todo_app.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
public class TodoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testCreateTodo() throws Exception {
        TodoDTO todoDTO = new TodoDTO(1L, "Task 1", "pending", LocalDate.now().plusDays(1), "P1");
        Mockito.when(todoService.createTodo(Mockito.any(TodoDTO.class))).thenReturn(todoDTO);

        mockMvc.perform(post("/add-task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.task").value("Task 1"))
                .andExpect(jsonPath("$.status").value("pending"));
    }
}
