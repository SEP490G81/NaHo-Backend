package org.naho.question.repository;

import org.naho.question.entity.AnswerHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerHistoryJpaRepository extends JpaRepository<AnswerHistoryEntity, Long> {
}
