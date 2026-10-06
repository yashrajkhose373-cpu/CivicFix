package com.studysync.service;

import com.studysync.dto.TaskRequestDto;
import com.studysync.dto.TaskResponseDto;
import com.studysync.entity.Priority;
import com.studysync.entity.Subject;
import com.studysync.entity.Task;
import com.studysync.entity.TaskStatus;
import com.studysync.entity.TaskType;
import com.studysync.exception.ResourceNotFoundException;
import com.studysync.repository.SubjectRepository;
import com.studysync.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final SubjectRepository subjectRepository;

    @Transactional(readOnly = true)
    public List<TaskResponseDto> getTasks(String search,
                                          TaskStatus status,
                                          Priority priority,
                                          TaskType type,
                                          Long subjectId) {
        String query = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        List<Task> tasks = taskRepository.searchTasks(query, status, priority, type, subjectId);
        return tasks.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return toDto(task);
    }

    @Transactional
    public TaskResponseDto createTask(TaskRequestDto dto) {
        Subject subject = null;
        if (dto.getSubjectId() != null) {
            subject = subjectRepository.findById(dto.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + dto.getSubjectId()));
        }

        Task task = Task.builder()
                .title(dto.getTitle().trim())
                .description(dto.getDescription())
                .type(dto.getType() != null ? dto.getType() : TaskType.TASK)
                .priority(dto.getPriority() != null ? dto.getPriority() : Priority.MEDIUM)
                .status(dto.getStatus() != null ? dto.getStatus() : TaskStatus.PENDING)
                .dueDate(dto.getDueDate())
                .estimatedMinutes(dto.getEstimatedMinutes())
                .subject(subject)
                .build();

        if (task.getStatus() == TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        }

        Task saved = taskRepository.save(task);
        return toDto(saved);
    }

    @Transactional
    public TaskResponseDto updateTask(Long id, TaskRequestDto dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        Subject subject = null;
        if (dto.getSubjectId() != null) {
            subject = subjectRepository.findById(dto.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + dto.getSubjectId()));
        }

        task.setTitle(dto.getTitle().trim());
        task.setDescription(dto.getDescription());
        if (dto.getType() != null) task.setType(dto.getType());
        if (dto.getPriority() != null) task.setPriority(dto.getPriority());

        if (dto.getStatus() != null) {
            if (dto.getStatus() == TaskStatus.COMPLETED && task.getStatus() != TaskStatus.COMPLETED) {
                task.markAsCompleted();
            } else if (dto.getStatus() != TaskStatus.COMPLETED && task.getStatus() == TaskStatus.COMPLETED) {
                task.markAsPending();
            }
            task.setStatus(dto.getStatus());
        }

        task.setDueDate(dto.getDueDate());
        task.setEstimatedMinutes(dto.getEstimatedMinutes());
        task.setSubject(subject);

        Task updated = taskRepository.save(task);
        return toDto(updated);
    }

    @Transactional
    public TaskResponseDto toggleTaskStatus(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (task.getStatus() == TaskStatus.COMPLETED) {
            task.markAsPending();
        } else {
            task.markAsCompleted();
        }

        Task updated = taskRepository.save(task);
        return toDto(updated);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskRepository.delete(task);
    }

    public TaskResponseDto toDto(Task task) {
        return TaskResponseDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .type(task.getType())
                .priority(task.getPriority())
                .status(task.getStatus())
                .dueDate(task.getDueDate())
                .estimatedMinutes(task.getEstimatedMinutes())
                .subjectId(task.getSubject() != null ? task.getSubject().getId() : null)
                .subjectName(task.getSubject() != null ? task.getSubject().getName() : null)
                .subjectCode(task.getSubject() != null ? task.getSubject().getCode() : null)
                .subjectColor(task.getSubject() != null ? task.getSubject().getColor() : null)
                .overdue(task.isOverdue())
                .completedAt(task.getCompletedAt())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
