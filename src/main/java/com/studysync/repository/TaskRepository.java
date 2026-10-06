package com.studysync.repository;

import com.studysync.entity.Priority;
import com.studysync.entity.Task;
import com.studysync.entity.TaskStatus;
import com.studysync.entity.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatusOrderByDueDateAsc(TaskStatus status);

    List<Task> findByTypeOrderByDueDateAsc(TaskType type);

    List<Task> findBySubjectId(Long subjectId);

    List<Task> findByPriority(Priority priority);

    @Query("SELECT t FROM Task t WHERE " +
           "(:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:type IS NULL OR t.type = :type) AND " +
           "(:subjectId IS NULL OR (t.subject IS NOT NULL AND t.subject.id = :subjectId)) " +
           "ORDER BY CASE WHEN t.dueDate IS NULL THEN 1 ELSE 0 END, t.dueDate ASC, t.createdAt DESC")
    List<Task> searchTasks(@Param("search") String search,
                           @Param("status") TaskStatus status,
                           @Param("priority") Priority priority,
                           @Param("type") TaskType type,
                           @Param("subjectId") Long subjectId);

    long countByStatus(TaskStatus status);

    long countByType(TaskType type);

    long countByTypeAndStatus(TaskType type, TaskStatus status);

    long countBySubjectId(Long subjectId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.dueDate < :now AND t.status != 'COMPLETED'")
    long countOverdueTasks(@Param("now") LocalDateTime now);

    @Query("SELECT t FROM Task t WHERE t.dueDate >= :now AND t.status != 'COMPLETED' ORDER BY t.dueDate ASC")
    List<Task> findUpcomingTasks(@Param("now") LocalDateTime now);

    @Query("SELECT t FROM Task t WHERE t.dueDate < :now AND t.status != 'COMPLETED' ORDER BY t.dueDate ASC")
    List<Task> findOverdueTasks(@Param("now") LocalDateTime now);

    List<Task> findTop5ByOrderByCreatedAtDesc();
}
