package com.studysync.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notes", indexes = {
    @Index(name = "idx_note_subject", columnList = "subject_id"),
    @Index(name = "idx_note_pinned", columnList = "is_pinned")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title cannot exceed 150 characters")
    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    @Size(max = 255, message = "Tags cannot exceed 255 characters")
    @Column(length = 255)
    private String tags; // Comma separated tags e.g. "midterm,chapter3,algorithms"

    @Column(name = "is_pinned", nullable = false)
    @Builder.Default
    private Boolean isPinned = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    @JsonIgnoreProperties({"tasks", "notes", "hibernateLazyInitializer", "handler"})
    private Subject subject;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
