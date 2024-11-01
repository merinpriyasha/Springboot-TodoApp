package com.priyasha.todo_app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "todos") //specifies the table name in db
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Auto increment id
    private Long id;

    @NotBlank(message = "Task name cannot be empty")
    @Column(name = "task", nullable = false) //Map the field to the db column
    private String task;

    @NotBlank(message = "Status cannot be empty")
    @Pattern(regexp = "^(pending|ongoing|completed)$", message = "Status must be either 'pending', 'ongoing' or 'completed'")
    @Column(name = "status", nullable = false) //Map the field to the db column
    private String status;

    @NotNull(message = "Deadline date cannot be null")
    @Future(message = "Deadline date must be in the future")
    @Column(name = "deadline_date", nullable = false) //Map the field to the db column
    private LocalDate deadlineDate;

}