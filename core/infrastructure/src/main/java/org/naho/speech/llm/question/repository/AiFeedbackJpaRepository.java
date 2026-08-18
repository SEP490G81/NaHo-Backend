package org.naho.speech.llm.question.repository;

import org.naho.speech.llm.model.question.AiFeedback;
import org.naho.speech.llm.question.entity.AiFeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiFeedbackJpaRepository extends JpaRepository<AiFeedbackEntity, Long> {
    AiFeedbackEntity save(AiFeedback aiFeedback);
}
