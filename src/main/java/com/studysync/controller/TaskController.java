package com.studysync.controller;

import com.studysync.dto.ApiResponse;
import com.studysync.dto.TaskRequestDto;
import com.studysync.dto.TaskResponseDto;
import com.studysync.entity.Priority;
import com.studysync.entity.TaskStatus;
import com.studysync.entity.TaskType;
import com.studysync.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskResponseDto>>> getTasks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) TaskType type,
            @RequestParam(required = false) Long subjectId) {
        List<TaskResponseDto> tasks = taskService.getTasks(search, status, priority, type, subjectId);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponseDto>> getTaskById(@PathVariable Long id) {
        TaskResponseDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(ApiResponse.success(task));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponseDto>> createTask(@Valid @RequestBody TaskRequestDto dto) {
        TaskResponseDto created = taskService.createTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Task created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponseDto>> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDto dto) {
        TaskResponseDto updated = taskService.updateTask(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", updated));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<TaskResponseDto>> toggleTaskStatus(@PathVariable Long id) {
        TaskResponseDto toggled = taskService.toggleTaskStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Task status updated", toggled));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully", null));
    }
}
