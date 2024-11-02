package com.priyasha.todo_app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoDTO {

    @Id
    private Long id;

    @NotBlank(message = "Task name cannot be empty")
    private String task;

    @NotBlank(message = "Status cannot be empty")
    @Pattern(regexp = "^(pending|ongoing|completed)$", message = "Status must be either 'pending', 'ongoing' or 'completed'")
    //the status field can only have one of the values: "pending", "completed", or "ongoing"
    private String status;

    @NotNull(message = "Deadline date cannot be null")
    @Future(message = "Deadline date must be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate deadlineDate ;

    @NotNull(message = "Priority cannot be empty")
    @Pattern(regexp = "^(P1|P2|P3|P4|P5)$", message = "Priority must be one of P1, P2, P3, P4, or P5")
    private String priority;

}
