package com.priyasha.todo_app.controller;

import com.priyasha.todo_app.dto.TodoDTO;
import com.priyasha.todo_app.service.TodoService;
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
    public ResponseEntity<TodoDTO> createTodo(@RequestBody TodoDTO todoDTO) {
        TodoDTO createTodo = todoService.createTodo(todoDTO);
        return ResponseEntity.ok(createTodo);
    }

    @PutMapping("/update-task/{id}")
    public ResponseEntity<TodoDTO> updateTodo(@PathVariable Long id, @RequestBody TodoDTO todoDTO) {
        TodoDTO updateTodo = todoService.updateTodo(id, todoDTO);
        return ResponseEntity.ok(updateTodo);
    }

    @DeleteMapping("/delete-task/{id}")
    public ResponseEntity<String> deleteTodo(@PathVariable Long id) {
        String response = todoService.deleteTodo(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-tasks")
    public ResponseEntity<Page<TodoDTO>> getTodos(@RequestParam(required = false) String keyword,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(required = false) String sortBy,
                                                  @RequestParam(required = false) String sortDirection,
                                                  @RequestParam(required = false) Integer page,
                                                  @RequestParam(required = false) Integer size) {
        Page<TodoDTO> todos = todoService.getTodos(keyword, status, sortBy, sortDirection, page, size);
        return ResponseEntity.ok(todos);

    }
}
