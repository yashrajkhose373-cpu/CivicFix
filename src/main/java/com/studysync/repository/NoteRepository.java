package com.studysync.repository;

import com.studysync.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findBySubjectId(Long subjectId);

    List<Note> findByIsPinnedTrueOrderByUpdatedAtDesc();

    @Query("SELECT n FROM Note n WHERE " +
           "(:search IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(n.content) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(n.tags) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:subjectId IS NULL OR (n.subject IS NOT NULL AND n.subject.id = :subjectId)) " +
           "ORDER BY n.isPinned DESC, n.updatedAt DESC")
    List<Note> searchNotes(@Param("search") String search, @Param("subjectId") Long subjectId);

    long countBySubjectId(Long subjectId);
}
