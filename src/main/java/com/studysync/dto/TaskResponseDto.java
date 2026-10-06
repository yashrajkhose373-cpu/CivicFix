package com.studysync.dto;

import com.studysync.entity.Priority;
import com.studysync.entity.TaskStatus;
import com.studysync.entity.TaskType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponseDto {

    private Long id;
    private String title;
    private String description;
    private TaskType type;
    private Priority priority;
    private TaskStatus status;
    private LocalDateTime dueDate;
    private Integer estimatedMinutes;
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    private String subjectColor;
    private boolean overdue;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
