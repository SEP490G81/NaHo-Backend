package org.naho.speech.llm.question.repository;

import org.naho.speech.llm.question.entity.UsedVocabularyAndGrammarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsedVocabularyAndGrammarJpaRepository extends JpaRepository<UsedVocabularyAndGrammarEntity, Long> {
    List<UsedVocabularyAndGrammarEntity> findByAiFeedbackId(Long aiFeedbackId);
}
