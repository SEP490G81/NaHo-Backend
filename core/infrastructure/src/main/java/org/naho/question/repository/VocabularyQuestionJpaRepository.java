package org.naho.question.repository;

import org.naho.question.entity.VocabularyQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabularyQuestionJpaRepository extends JpaRepository<VocabularyQuestionEntity, Long> {
}
