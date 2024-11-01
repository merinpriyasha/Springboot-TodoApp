package com.priyasha.todo_app.service;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.priyasha.todo_app.dto.TodoDTO;
import com.priyasha.todo_app.model.Todo;
import com.priyasha.todo_app.repository.TodoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TodoService(TodoRepository todoRepository, ModelMapper modelMapper) {
        this.todoRepository = todoRepository;
        this.modelMapper = modelMapper;
    }

    //Create new Todo
    public TodoDTO createTodo(TodoDTO todoDTO) {
        try {
            // Map the TodoDTO to Todo entity
            Todo todo = modelMapper.map(todoDTO, Todo.class);
            // Save the entity and map the saved entity back to TodoDTO for return
            return modelMapper.map(todoRepository.save(todo), TodoDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create todo item", e);
        }

    }

    //Update existing Todo
    public TodoDTO updateTodo(Long id, TodoDTO todoDTO) {

        try {
            Optional<Todo> existingTodo = todoRepository.findById(id);
            if (existingTodo.isPresent()) {
                Todo todoToUpdate = existingTodo.get();
                modelMapper.map(todoDTO, todoToUpdate); // Update fields of existingTodo with fields from todoDTO
                return modelMapper.map(todoRepository.save(todoToUpdate), TodoDTO.class);
            } else {
                throw new EntityNotFoundException("Todo with id" + id + "not found");
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to update todo item", e);
        }
    }

    //Delete Todo
    @Transactional
    public String deleteTodo(Long id) {
        try {
            if (todoRepository.existsById(id)) {
                todoRepository.deleteById(id);
                return "Todo with id " + id + " was successfully deleted.";
            } else {
                throw new EntityNotFoundException("Todo with id " + id + "not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete todo item", e);
        }
    }

    //Get Todo
    public Page<TodoDTO> getTodos(String keyword, String status, String sortBy, String sortDirection, Integer page, Integer size) {

        // Default values for pagination
        int defaultPage = 0; // Default page number
        int defaultSize = 10; // Default page size
        int currentPage = (page != null) ? page : defaultPage;
        int pageSize = (size != null) ? size : defaultSize;

        // Default sorting: by id in ascending order
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        if (sortBy != null && !sortBy.isEmpty()) {
            // If sortDirection is provided, apply it to sortBy
            if (sortDirection != null && !sortDirection.isEmpty()) {
                sort = "desc".equalsIgnoreCase(sortDirection)
                        ? Sort.by(Sort.Direction.DESC, sortBy)
                        : Sort.by(Sort.Direction.ASC, sortBy);
            } else {
                // SortBy is provided without sortDirection, default to ascending
                sort = Sort.by(Sort.Direction.ASC, sortBy);
            }
        } else if (sortDirection != null && !sortDirection.isEmpty()) {
            // If sortDirection is provided without sortBy, sort by id
            sort = "desc".equalsIgnoreCase(sortDirection) ? Sort.by(Sort.Direction.DESC, "id") : Sort.by(Sort.Direction.ASC, "id");
        }

        Pageable pageable = PageRequest.of(currentPage, pageSize, sort);

        try {
            Page<Todo> todoPage;
            if (keyword != null && !keyword.isEmpty()) {
                todoPage = todoRepository.findByTaskContainingIgnoreCase(keyword, pageable);
            } else if (status != null && !status.isEmpty()) {
                todoPage = todoRepository.findByStatus(status, pageable);
            } else {
                todoPage = todoRepository.findAll(pageable);
            }

            List<TodoDTO> todoDTOS = modelMapper.map(todoPage.getContent(), new TypeToken<List<TodoDTO>>() {
            }.getType());
            return new PageImpl<>(todoDTOS, pageable, todoPage.getTotalElements());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve todos", e);
        }

    }

}
