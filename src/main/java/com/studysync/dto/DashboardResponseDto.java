package com.studysync.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponseDto {

    private long totalTasks;
    private long pendingTasks;
    private long inProgressTasks;
    private long completedTasks;
    private long overdueTasks;
    private double completionRate;

    private long totalAssignments;
    private long pendingAssignments;
    private long totalNotes;
    private long totalSubjects;

    private List<TaskResponseDto> upcomingDeadlines;
    private List<TaskResponseDto> overdueDeadlines;
    private List<TaskResponseDto> recentTasks;
    private List<NoteResponseDto> pinnedNotes;

    private Map<String, Long> tasksByPriority;
    private Map<String, Long> tasksBySubject;
}
