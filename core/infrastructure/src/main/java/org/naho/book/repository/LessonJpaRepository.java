package org.naho.book.repository;

import org.naho.book.entity.LessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonJpaRepository extends JpaRepository<LessonEntity, Long> {
    List<LessonEntity> findByTopicId(Long topicId);
}
