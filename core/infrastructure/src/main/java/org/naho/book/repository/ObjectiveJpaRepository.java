package org.naho.book.repository;

import org.naho.book.entity.ObjectiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjectiveJpaRepository extends JpaRepository<ObjectiveEntity, Long> {
    List<ObjectiveEntity> findByLessonId(Long lessonId);
}
