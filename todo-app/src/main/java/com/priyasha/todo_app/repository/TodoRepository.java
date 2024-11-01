package com.priyasha.todo_app.repository;

import com.priyasha.todo_app.model.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    //search for todo items based on keywords
    Page<Todo> findByTaskContainingIgnoreCase(String keyword, Pageable pageable);

    //find todos by completion status
    Page<Todo> findByStatus(String status, Pageable pageable);

}
