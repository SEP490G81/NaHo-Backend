package org.naho.speech.llm.question.repository;

import org.naho.speech.llm.question.entity.UserAnswerErrorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAnswerErrorJpaRepository extends JpaRepository<UserAnswerErrorEntity, Long> {
    List<UserAnswerErrorEntity> findByAiFeedbackId(Long aiFeedbackId);
}
