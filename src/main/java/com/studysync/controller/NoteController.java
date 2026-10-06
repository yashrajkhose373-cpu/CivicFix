package com.studysync.controller;

import com.studysync.dto.ApiResponse;
import com.studysync.dto.NoteRequestDto;
import com.studysync.dto.NoteResponseDto;
import com.studysync.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NoteResponseDto>>> getNotes(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long subjectId) {
        List<NoteResponseDto> notes = noteService.getNotes(search, subjectId);
        return ResponseEntity.ok(ApiResponse.success(notes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NoteResponseDto>> getNoteById(@PathVariable Long id) {
        NoteResponseDto note = noteService.getNoteById(id);
        return ResponseEntity.ok(ApiResponse.success(note));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NoteResponseDto>> createNote(@Valid @RequestBody NoteRequestDto dto) {
        NoteResponseDto created = noteService.createNote(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Note created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NoteResponseDto>> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody NoteRequestDto dto) {
        NoteResponseDto updated = noteService.updateNote(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Note updated successfully", updated));
    }

    @PatchMapping("/{id}/pin")
    public ResponseEntity<ApiResponse<NoteResponseDto>> togglePin(@PathVariable Long id) {
        NoteResponseDto toggled = noteService.togglePin(id);
        return ResponseEntity.ok(ApiResponse.success("Note pin status updated", toggled));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
        return ResponseEntity.ok(ApiResponse.success("Note deleted successfully", null));
    }
}
