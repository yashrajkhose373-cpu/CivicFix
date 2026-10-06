package com.studysync.service;

import com.studysync.dto.SubjectDto;
import com.studysync.entity.Subject;
import com.studysync.exception.BadRequestException;
import com.studysync.exception.ResourceNotFoundException;
import com.studysync.repository.NoteRepository;
import com.studysync.repository.SubjectRepository;
import com.studysync.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final TaskRepository taskRepository;
    private final NoteRepository noteRepository;

    @Transactional(readOnly = true)
    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubjectDto getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));
        return toDto(subject);
    }

    @Transactional
    public SubjectDto createSubject(SubjectDto dto) {
        if (subjectRepository.existsByNameIgnoreCase(dto.getName().trim())) {
            throw new BadRequestException("Subject with name '" + dto.getName() + "' already exists");
        }

        Subject subject = Subject.builder()
                .name(dto.getName().trim())
                .code(dto.getCode())
                .color(dto.getColor() != null && !dto.getColor().isBlank() ? dto.getColor() : "#4f46e5")
                .instructor(dto.getInstructor())
                .build();

        Subject saved = subjectRepository.save(subject);
        return toDto(saved);
    }

    @Transactional
    public SubjectDto updateSubject(Long id, SubjectDto dto) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));

        if (!subject.getName().equalsIgnoreCase(dto.getName().trim()) &&
                subjectRepository.existsByNameIgnoreCase(dto.getName().trim())) {
            throw new BadRequestException("Subject with name '" + dto.getName() + "' already exists");
        }

        subject.setName(dto.getName().trim());
        subject.setCode(dto.getCode());
        if (dto.getColor() != null && !dto.getColor().isBlank()) {
            subject.setColor(dto.getColor());
        }
        subject.setInstructor(dto.getInstructor());

        Subject updated = subjectRepository.save(subject);
        return toDto(updated);
    }

    @Transactional
    public void deleteSubject(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));
        subjectRepository.delete(subject);
    }

    public SubjectDto toDto(Subject subject) {
        long taskCount = taskRepository.countBySubjectId(subject.getId());
        long noteCount = noteRepository.countBySubjectId(subject.getId());

        return SubjectDto.builder()
                .id(subject.getId())
                .name(subject.getName())
                .code(subject.getCode())
                .color(subject.getColor())
                .instructor(subject.getInstructor())
                .taskCount(taskCount)
                .noteCount(noteCount)
                .createdAt(subject.getCreatedAt())
                .build();
    }
}
