package com.studysync.dto;

import com.studysync.entity.Priority;
import com.studysync.entity.TaskStatus;
import com.studysync.entity.TaskType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequestDto {

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 150, message = "Title must be between 2 and 150 characters")
    private String title;

    private String description;

    @NotNull(message = "Task type is required (TASK, ASSIGNMENT, EXAM_PREP, PROJECT, READING)")
    private TaskType type;

    @NotNull(message = "Priority is required (LOW, MEDIUM, HIGH, URGENT)")
    private Priority priority;

    private TaskStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dueDate;

    private Integer estimatedMinutes;

    private Long subjectId;
}
