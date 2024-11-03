package com.priyasha.todo_app.controller;

import com.priyasha.todo_app.dto.TodoDTO;
import com.priyasha.todo_app.service.TodoService;
import com.priyasha.todo_app.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${todo.base-url}")
public class TodoController {

    private final TodoService todoService;
    private final JwtUtil jwtUtil;

    /**
     *
     * @param todoService
     * @param jwtUtil
     */
    @Autowired
    public TodoController(TodoService todoService, JwtUtil jwtUtil) {

        this.todoService = todoService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/add-task")
    public ResponseEntity<TodoDTO> createTodo(@Valid @RequestBody TodoDTO todoDTO, @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", ""); // Remove "Bearer " from the header
        TodoDTO createTodo = todoService.createTodo(todoDTO, token);

        return ResponseEntity.ok(createTodo);

    }

    @PutMapping("/update-task/{id}")
    public ResponseEntity<TodoDTO> updateTodo(@PathVariable Long id,@Valid @RequestBody TodoDTO todoDTO, @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", ""); // Remove "Bearer " from the header
        TodoDTO updateTodo = todoService.updateTodo(id, todoDTO, token);

        return ResponseEntity.ok(updateTodo);
    }

    @DeleteMapping("/delete-task/{id}")
    public ResponseEntity<String> deleteTodo(@PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", ""); // Remove "Bearer " from the header
        String response = todoService.deleteTodo(id, token);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-tasks")
    public ResponseEntity<Page<TodoDTO>> getTodos(@Valid @RequestParam(required = false) String keyword,
                                                  @Valid @RequestParam(required = false) String status,
                                                  @Valid @RequestParam(required = false) String sortBy,
                                                  @Valid @RequestParam(required = false) String sortDirection,
                                                  @Valid @RequestParam(required = false) Integer page,
                                                  @Valid @RequestParam(required = false) Integer size,
                                                  @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", ""); // Remove "Bearer " from the header
        Page<TodoDTO> todos = todoService.getTodos(keyword, status, sortBy, sortDirection, page, size, token);

        return ResponseEntity.ok(todos);

    }
}
