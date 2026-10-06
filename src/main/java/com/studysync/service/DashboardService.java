package com.studysync.service;

import com.studysync.dto.DashboardResponseDto;
import com.studysync.dto.NoteResponseDto;
import com.studysync.dto.TaskResponseDto;
import com.studysync.entity.Priority;
import com.studysync.entity.Subject;
import com.studysync.entity.Task;
import com.studysync.entity.TaskStatus;
import com.studysync.entity.TaskType;
import com.studysync.repository.NoteRepository;
import com.studysync.repository.SubjectRepository;
import com.studysync.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TaskRepository taskRepository;
    private final NoteRepository noteRepository;
    private final SubjectRepository subjectRepository;
    private final TaskService taskService;
    private final NoteService noteService;

    @Transactional(readOnly = true)
    public DashboardResponseDto getDashboardData() {
        LocalDateTime now = LocalDateTime.now();

        long totalTasks = taskRepository.count();
        long pendingTasks = taskRepository.countByStatus(TaskStatus.PENDING);
        long inProgressTasks = taskRepository.countByStatus(TaskStatus.IN_PROGRESS);
        long completedTasks = taskRepository.countByStatus(TaskStatus.COMPLETED);
        long overdueTasks = taskRepository.countOverdueTasks(now);

        double completionRate = totalTasks > 0 ? ((double) completedTasks / totalTasks) * 100.0 : 0.0;
        completionRate = Math.round(completionRate * 10.0) / 10.0;

        long totalAssignments = taskRepository.countByType(TaskType.ASSIGNMENT);
        long pendingAssignments = taskRepository.countByTypeAndStatus(TaskType.ASSIGNMENT, TaskStatus.PENDING)
                + taskRepository.countByTypeAndStatus(TaskType.ASSIGNMENT, TaskStatus.IN_PROGRESS);

        long totalNotes = noteRepository.count();
        long totalSubjects = subjectRepository.count();

        List<TaskResponseDto> upcomingDeadlines = taskRepository.findUpcomingTasks(now).stream()
                .limit(6)
                .map(taskService::toDto)
                .collect(Collectors.toList());

        List<TaskResponseDto> overdueDeadlines = taskRepository.findOverdueTasks(now).stream()
                .limit(6)
                .map(taskService::toDto)
                .collect(Collectors.toList());

        List<TaskResponseDto> recentTasks = taskRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(taskService::toDto)
                .collect(Collectors.toList());

        List<NoteResponseDto> pinnedNotes = noteRepository.findByIsPinnedTrueOrderByUpdatedAtDesc().stream()
                .limit(4)
                .map(noteService::toDto)
                .collect(Collectors.toList());

        Map<String, Long> tasksByPriority = new HashMap<>();
        for (Priority p : Priority.values()) {
            tasksByPriority.put(p.name(), (long) taskRepository.findByPriority(p).size());
        }

        Map<String, Long> tasksBySubject = new HashMap<>();
        List<Subject> subjects = subjectRepository.findAll();
        for (Subject s : subjects) {
            tasksBySubject.put(s.getName(), taskRepository.countBySubjectId(s.getId()));
        }

        return DashboardResponseDto.builder()
                .totalTasks(totalTasks)
                .pendingTasks(pendingTasks)
                .inProgressTasks(inProgressTasks)
                .completedTasks(completedTasks)
                .overdueTasks(overdueTasks)
                .completionRate(completionRate)
                .totalAssignments(totalAssignments)
                .pendingAssignments(pendingAssignments)
                .totalNotes(totalNotes)
                .totalSubjects(totalSubjects)
                .upcomingDeadlines(upcomingDeadlines)
                .overdueDeadlines(overdueDeadlines)
                .recentTasks(recentTasks)
                .pinnedNotes(pinnedNotes)
                .tasksByPriority(tasksByPriority)
                .tasksBySubject(tasksBySubject)
                .build();
    }
}
