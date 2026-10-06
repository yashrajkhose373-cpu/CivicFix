package com.studysync.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteResponseDto {

    private Long id;
    private String title;
    private String content;
    private String tags;
    private Boolean isPinned;
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    private String subjectColor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
