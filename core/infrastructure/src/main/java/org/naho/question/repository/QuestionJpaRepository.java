package org.naho.question.repository;

import org.naho.question.entity.QuestionEntity;
import org.naho.question.type.QuestionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionJpaRepository extends JpaRepository<QuestionEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(ah) > 0 THEN true ELSE false END FROM AnswerHistoryEntity ah WHERE ah.question.objective.lesson.topic.id = :topicId")
    boolean existsAnswerHistoryByTopicId(@Param("topicId") Long topicId);

    @Query("SELECT CASE WHEN COUNT(ah) > 0 THEN true ELSE false END FROM AnswerHistoryEntity ah WHERE ah.question.id = :questionId")
    boolean existsAnswerHistoryByQuestionId(@Param("questionId") Long questionId);

    @Modifying
    @Query("DELETE FROM QuestionEntity q WHERE q.objective.lesson.topic.id = :topicId")
    void deleteAllByTopicId(@Param("topicId") Long topicId);

    @Modifying
    @Query("UPDATE QuestionEntity q SET q.status = :status WHERE q.objective.lesson.topic.id = :topicId")
    void updateStatusByTopicId(@Param("topicId") Long topicId, @Param("status") QuestionStatus status);

    @Query("SELECT MAX(q.orderIndex) FROM QuestionEntity q WHERE q.objective.id = :objectiveId")
    Double getMaxOrderIndexByObjectiveId(@Param("objectiveId") Long objectiveId);

    boolean existsByObjectiveIdAndTitle(Long objectiveId, String title);

    boolean existsByObjectiveIdAndTitleAndIdNot(Long objectiveId, String title, Long id);
}
