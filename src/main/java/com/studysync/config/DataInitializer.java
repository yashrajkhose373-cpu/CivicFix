package com.studysync.config;

import com.studysync.entity.*;
import com.studysync.repository.NoteRepository;
import com.studysync.repository.SubjectRepository;
import com.studysync.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final SubjectRepository subjectRepository;
    private final TaskRepository taskRepository;
    private final NoteRepository noteRepository;

    @Bean
    public CommandLineRunner initSampleData() {
        return args -> {
            if (subjectRepository.count() > 0) {
                log.info("Database already contains data, skipping sample data initialization.");
                return;
            }

            log.info("Initializing StudySync with professional sample student data...");

            // 1. Create Academic Subjects
            Subject cs = Subject.builder()
                    .name("Data Structures & Algorithms")
                    .code("CS-301")
                    .color("#6366f1") // Indigo
                    .instructor("Prof. Alan Turing")
                    .build();

            Subject db = Subject.builder()
                    .name("Database Management Systems")
                    .code("CS-304")
                    .color("#0ea5e9") // Sky Blue
                    .instructor("Dr. Edgar Codd")
                    .build();

            Subject math = Subject.builder()
                    .name("Linear Algebra & Calculus")
                    .code("MATH-202")
                    .color("#ec4899") // Pink
                    .instructor("Prof. Emmy Noether")
                    .build();

            Subject web = Subject.builder()
                    .name("Cloud Architecture & Microservices")
                    .code("CS-410")
                    .color("#10b981") // Emerald
                    .instructor("Dr. Leslie Lamport")
                    .build();

            Subject ai = Subject.builder()
                    .name("Artificial Intelligence & ML")
                    .code("CS-450")
                    .color("#f59e0b") // Amber
                    .instructor("Prof. Geoffrey Hinton")
                    .build();

            List<Subject> savedSubjects = subjectRepository.saveAll(List.of(cs, db, math, web, ai));
            Subject sCs = savedSubjects.get(0);
            Subject sDb = savedSubjects.get(1);
            Subject sMath = savedSubjects.get(2);
            Subject sWeb = savedSubjects.get(3);
            Subject sAi = savedSubjects.get(4);

            LocalDateTime now = LocalDateTime.now();

            // 2. Create Tasks & Assignments with varying deadlines & priorities
            Task t1 = Task.builder()
                    .title("Implement Red-Black Tree Balancing")
                    .description("Complete the deletion algorithm and rotation unit tests in Java.")
                    .type(TaskType.ASSIGNMENT)
                    .priority(Priority.URGENT)
                    .status(TaskStatus.IN_PROGRESS)
                    .dueDate(now.plusDays(1).withHour(23).withMinute(59))
                    .estimatedMinutes(180)
                    .subject(sCs)
                    .build();

            Task t2 = Task.builder()
                    .title("Design Relational Schema for E-Commerce App")
                    .description("Create 3NF normalized tables, ER diagram, and write DDL scripts with foreign keys.")
                    .type(TaskType.PROJECT)
                    .priority(Priority.HIGH)
                    .status(TaskStatus.PENDING)
                    .dueDate(now.plusDays(3).withHour(18).withMinute(0))
                    .estimatedMinutes(240)
                    .subject(sDb)
                    .build();

            Task t3 = Task.builder()
                    .title("Prepare for Midterm Quiz on Eigenvalues")
                    .description("Review chapters 4 and 5, solve 10 practice problems on diagonal matrices.")
                    .type(TaskType.EXAM_PREP)
                    .priority(Priority.HIGH)
                    .status(TaskStatus.PENDING)
                    .dueDate(now.plusDays(4).withHour(9).withMinute(30))
                    .estimatedMinutes(120)
                    .subject(sMath)
                    .build();

            Task t4 = Task.builder()
                    .title("Deploy Spring Boot API to Docker Container")
                    .description("Write multi-stage Dockerfile and test locally with Docker Compose.")
                    .type(TaskType.ASSIGNMENT)
                    .priority(Priority.MEDIUM)
                    .status(TaskStatus.COMPLETED)
                    .completedAt(now.minusDays(1))
                    .dueDate(now.minusDays(1).withHour(17).withMinute(0))
                    .estimatedMinutes(90)
                    .subject(sWeb)
                    .build();

            Task t5 = Task.builder()
                    .title("Read Attention Is All You Need Paper")
                    .description("Take notes on self-attention mechanism, transformer encoder-decoder stack.")
                    .type(TaskType.READING)
                    .priority(Priority.LOW)
                    .status(TaskStatus.PENDING)
                    .dueDate(now.plusDays(6).withHour(20).withMinute(0))
                    .estimatedMinutes(60)
                    .subject(sAi)
                    .build();

            Task t6 = Task.builder()
                    .title("Submit SQL Query Optimization Report")
                    .description("Benchmark index scans vs table scans using EXPLAIN ANALYZE on 1M rows.")
                    .type(TaskType.ASSIGNMENT)
                    .priority(Priority.URGENT)
                    .status(TaskStatus.PENDING)
                    .dueDate(now.minusHours(5)) // Overdue!
                    .estimatedMinutes(150)
                    .subject(sDb)
                    .build();

            Task t7 = Task.builder()
                    .title("Graph Traversal Dijkstra Algorithm Quiz")
                    .description("Brush up on priority queues, adjacency lists, and edge relaxation.")
                    .type(TaskType.EXAM_PREP)
                    .priority(Priority.MEDIUM)
                    .status(TaskStatus.PENDING)
                    .dueDate(now.plusDays(2).withHour(14).withMinute(0))
                    .estimatedMinutes(90)
                    .subject(sCs)
                    .build();

            taskRepository.saveAll(List.of(t1, t2, t3, t4, t5, t6, t7));

            // 3. Create Study Notes
            Note n1 = Note.builder()
                    .title("B-Tree vs B+ Tree Comparison")
                    .content("### Key Differences:\n\n1. **Data Pointers**:\n   - B-Tree stores data keys & records in internal AND leaf nodes.\n   - B+ Tree stores all data keys in leaf nodes; internal nodes only store navigation keys.\n\n2. **Sequential Access**:\n   - B+ Tree leaf nodes are linked via a doubly linked list, making range queries O(log N + K).\n\n3. **Height & Fanout**:\n   - B+ Tree has higher branching factor because internal nodes have smaller footprints.")
                    .tags("database,indexing,b-tree,interview")
                    .isPinned(true)
                    .subject(sDb)
                    .build();

            Note n2 = Note.builder()
                    .title("Dynamic Programming Patterns & Cheat Sheet")
                    .content("### Standard DP Frameworks:\n\n1. **0/1 Knapsack**: Pick or skip items. State: `dp[i][w] = max(dp[i-1][w], val[i] + dp[i-1][w-wt[i]])`\n2. **Longest Common Subsequence (LCS)**: Match characters from end.\n3. **Matrix Chain Multiplication / Interval DP**.\n4. **Tree DP**: Post-order traversal storing subtree states.")
                    .tags("algorithms,dp,dsa,cheatsheet")
                    .isPinned(true)
                    .subject(sCs)
                    .build();

            Note n3 = Note.builder()
                    .title("Linear Transformations & Matrix Rank")
                    .content("Rank-Nullity Theorem: `dim(Domain) = Rank(T) + Nullity(T)`. When nullity is 0, the transformation is injective (one-to-one).")
                    .tags("math,linear-algebra,matrices")
                    .isPinned(false)
                    .subject(sMath)
                    .build();

            Note n4 = Note.builder()
                    .title("Spring Boot Bean Lifecycle & Scopes")
                    .content("Default scope is Singleton. Prototype creates new instance per injection. Use `@PostConstruct` and `@PreDestroy` for initialization and cleanup hooks.")
                    .tags("spring-boot,java,architecture")
                    .isPinned(false)
                    .subject(sWeb)
                    .build();

            noteRepository.saveAll(List.of(n1, n2, n3, n4));

            log.info("StudySync sample data initialized successfully with {} subjects, {} tasks, {} notes.",
                    savedSubjects.size(), 7, 4);
        };
    }
}
