package com.studysync.controller;

import com.studysync.dto.ApiResponse;
import com.studysync.dto.SubjectDto;
import com.studysync.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectDto>>> getAllSubjects() {
        List<SubjectDto> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(ApiResponse.success(subjects));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectDto>> getSubjectById(@PathVariable Long id) {
        SubjectDto subject = subjectService.getSubjectById(id);
        return ResponseEntity.ok(ApiResponse.success(subject));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubjectDto>> createSubject(@Valid @RequestBody SubjectDto dto) {
        SubjectDto created = subjectService.createSubject(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subject created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectDto>> updateSubject(
            @PathVariable Long id,
            @Valid @RequestBody SubjectDto dto) {
        SubjectDto updated = subjectService.updateSubject(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Subject updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok(ApiResponse.success("Subject deleted successfully", null));
    }
}
