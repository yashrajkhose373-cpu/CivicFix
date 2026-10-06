package com.studysync.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subjects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Subject name is required")
    @Size(max = 100, message = "Subject name cannot exceed 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Size(max = 50, message = "Course code cannot exceed 50 characters")
    @Column(length = 50)
    private String code;

    @Size(max = 20, message = "Color code cannot exceed 20 characters")
    @Column(length = 20)
    private String color; // e.g. hex #6366f1

    @Size(max = 100, message = "Instructor name cannot exceed 100 characters")
    @Column(length = 100)
    private String instructor;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private List<Note> notes = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
