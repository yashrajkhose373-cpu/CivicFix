package com.studysync.repository;

import com.studysync.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
