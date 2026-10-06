package com.studysync.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectDto {

    private Long id;

    @NotBlank(message = "Subject name is required")
    @Size(max = 100, message = "Subject name cannot exceed 100 characters")
    private String name;

    private String code;
    private String color;
    private String instructor;
    private Long taskCount;
    private Long noteCount;
    private LocalDateTime createdAt;
}
