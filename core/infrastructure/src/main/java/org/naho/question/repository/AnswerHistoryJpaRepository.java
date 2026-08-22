package org.naho.question.repository;

import org.naho.question.entity.AnswerHistoryEntity;
import org.naho.shared.persistence.BaseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerHistoryJpaRepository extends BaseJpaRepository<AnswerHistoryEntity> {
    List<AnswerHistoryEntity> findAllBySpeakingQuestion_Id(Long speakingQuestionId);

    List<AnswerHistoryEntity> findAllBySpeakingQuestion_IdAndUser_Id(Long speakingQuestionId, Long userId);

    Optional<AnswerHistoryEntity> findByIdAndUser_Id(Long id, Long userId);
}
