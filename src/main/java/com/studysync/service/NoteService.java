package com.studysync.service;

import com.studysync.dto.NoteRequestDto;
import com.studysync.dto.NoteResponseDto;
import com.studysync.entity.Note;
import com.studysync.entity.Subject;
import com.studysync.exception.ResourceNotFoundException;
import com.studysync.repository.NoteRepository;
import com.studysync.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final SubjectRepository subjectRepository;

    @Transactional(readOnly = true)
    public List<NoteResponseDto> getNotes(String search, Long subjectId) {
        String query = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        return noteRepository.searchNotes(query, subjectId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NoteResponseDto getNoteById(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
        return toDto(note);
    }

    @Transactional
    public NoteResponseDto createNote(NoteRequestDto dto) {
        Subject subject = null;
        if (dto.getSubjectId() != null) {
            subject = subjectRepository.findById(dto.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + dto.getSubjectId()));
        }

        Note note = Note.builder()
                .title(dto.getTitle().trim())
                .content(dto.getContent())
                .tags(dto.getTags())
                .isPinned(dto.getIsPinned() != null ? dto.getIsPinned() : false)
                .subject(subject)
                .build();

        Note saved = noteRepository.save(note);
        return toDto(saved);
    }

    @Transactional
    public NoteResponseDto updateNote(Long id, NoteRequestDto dto) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));

        Subject subject = null;
        if (dto.getSubjectId() != null) {
            subject = subjectRepository.findById(dto.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + dto.getSubjectId()));
        }

        note.setTitle(dto.getTitle().trim());
        note.setContent(dto.getContent());
        note.setTags(dto.getTags());
        if (dto.getIsPinned() != null) {
            note.setIsPinned(dto.getIsPinned());
        }
        note.setSubject(subject);

        Note updated = noteRepository.save(note);
        return toDto(updated);
    }

    @Transactional
    public NoteResponseDto togglePin(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));

        note.setIsPinned(!Boolean.TRUE.equals(note.getIsPinned()));
        Note updated = noteRepository.save(note);
        return toDto(updated);
    }

    @Transactional
    public void deleteNote(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
        noteRepository.delete(note);
    }

    public NoteResponseDto toDto(Note note) {
        return NoteResponseDto.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .tags(note.getTags())
                .isPinned(note.getIsPinned())
                .subjectId(note.getSubject() != null ? note.getSubject().getId() : null)
                .subjectName(note.getSubject() != null ? note.getSubject().getName() : null)
                .subjectCode(note.getSubject() != null ? note.getSubject().getCode() : null)
                .subjectColor(note.getSubject() != null ? note.getSubject().getColor() : null)
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }
}
