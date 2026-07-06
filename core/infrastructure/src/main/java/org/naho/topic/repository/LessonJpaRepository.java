package org.naho.topic.repository;

import org.naho.topic.entity.LessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonJpaRepository extends JpaRepository<LessonEntity, Long> {
}
