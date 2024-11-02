package com.priyasha.todo_app.service;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.priyasha.todo_app.dto.TodoDTO;
import com.priyasha.todo_app.model.Todo;
import com.priyasha.todo_app.repository.TodoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.hibernate.mapping.Collection;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    private static final Logger logger = LoggerFactory.getLogger(TodoService.class);
    private final TodoRepository todoRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TodoService(TodoRepository todoRepository, ModelMapper modelMapper) {
        this.todoRepository = todoRepository;
        this.modelMapper = modelMapper;
    }

    //Create new Todo
    public TodoDTO createTodo(TodoDTO todoDTO) {
        logger.info("Creating a new Todo item with task: {}", todoDTO.getTask());
        try {
            // Map the TodoDTO to Todo entity
            Todo todo = modelMapper.map(todoDTO, Todo.class);
            // Save the entity and map the saved entity back to TodoDTO for return
            return modelMapper.map(todoRepository.save(todo), TodoDTO.class);
        } catch (Exception e) {
            logger.error("Failed to create todo item", e);
            throw new RuntimeException("Failed to create todo item", e);
        }

    }

    //Update existing Todo
    public TodoDTO updateTodo(Long id, TodoDTO todoDTO) {
        logger.info("Updating Todo item with id: {}", id);
        try {
            Optional<Todo> existingTodo = todoRepository.findById(id);
            if (existingTodo.isPresent()) {
                Todo todoToUpdate = existingTodo.get();

                // Manually set fields instead of mapping the whole DTO
                todoToUpdate.setTask(todoDTO.getTask());
                todoToUpdate.setStatus(todoDTO.getStatus());
                todoToUpdate.setDeadlineDate(todoDTO.getDeadlineDate());
                todoToUpdate.setPriority(todoDTO.getPriority());

                return modelMapper.map(todoRepository.save(todoToUpdate), TodoDTO.class);
            } else {
                logger.warn("Todo with id {} not found for update", id);
                throw new EntityNotFoundException("Todo with id" + id + "not found");
            }

        } catch (EntityNotFoundException e) {
            throw e; // Can be caught by a specific exception handler if defined in the controller
        } catch (Exception e) {
            logger.error("Failed to update todo item with id {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to update todo item with id " + id, e);
        }
    }

    //Delete Todo
    @Transactional
    public String deleteTodo(Long id) {
        logger.info("Attempting to delete Todo with id: {}", id);
        try {
            if (todoRepository.existsById(id)) {
                todoRepository.deleteById(id);
                return "Todo with id " + id + " was successfully deleted.";
            } else {
                logger.warn("Todo with id {} not found for delete", id);
                throw new EntityNotFoundException("Todo with id " + id + "not found");
            }
        } catch (Exception e) {
            logger.error("Failed to delete todo item with id {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete todo item", e);
        }
    }

    //Get Todo
    public Page<TodoDTO> getTodos(String keyword, String status, String sortBy, String sortDirection, Integer page, Integer size) {

        logger.info("Fetching todos with filter - keyword: {}, status: {}, sortBy: {}, sortDirection: {}", keyword, status, sortBy, sortDirection);

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
            return new PageImpl<>(todoDTOS != null ? todoDTOS : Collections.emptyList(), pageable, todoPage.getTotalElements());
        } catch (Exception e) {
            logger.error("Failed to get todo items", e);
            throw new RuntimeException("Failed to retrieve todos", e);
        }

    }

}
