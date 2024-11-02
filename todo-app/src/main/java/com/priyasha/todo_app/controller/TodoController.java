package com.priyasha.todo_app.controller;

import com.priyasha.todo_app.dto.TodoDTO;
import com.priyasha.todo_app.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/todos")
public class TodoController {

    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping("/add-task")
    public ResponseEntity<TodoDTO> createTodo(@Valid @RequestBody TodoDTO todoDTO) {
        TodoDTO createTodo = todoService.createTodo(todoDTO);
        return ResponseEntity.ok(createTodo);
    }

    @PutMapping("/update-task/{id}")
    public ResponseEntity<TodoDTO> updateTodo(@PathVariable Long id,@Valid @RequestBody TodoDTO todoDTO) {
        TodoDTO updateTodo = todoService.updateTodo(id, todoDTO);
        return ResponseEntity.ok(updateTodo);
    }

    @DeleteMapping("/delete-task/{id}")
    public ResponseEntity<String> deleteTodo(@PathVariable Long id) {
        String response = todoService.deleteTodo(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-tasks")
    public ResponseEntity<Page<TodoDTO>> getTodos(@Valid @RequestParam(required = false) String keyword,
                                                  @Valid @RequestParam(required = false) String status,
                                                  @Valid @RequestParam(required = false) String sortBy,
                                                  @Valid @RequestParam(required = false) String sortDirection,
                                                  @Valid @RequestParam(required = false) Integer page,
                                                  @Valid @RequestParam(required = false) Integer size) {
        Page<TodoDTO> todos = todoService.getTodos(keyword, status, sortBy, sortDirection, page, size);
        return ResponseEntity.ok(todos);

    }
}
