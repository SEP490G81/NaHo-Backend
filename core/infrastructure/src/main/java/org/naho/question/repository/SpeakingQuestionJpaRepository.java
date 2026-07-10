package org.naho.question.repository;

import org.naho.question.entity.SpeakingQuestionEntity;
import org.naho.question.type.QuestionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpeakingQuestionJpaRepository extends JpaRepository<SpeakingQuestionEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(ah) > 0 THEN true ELSE false END FROM AnswerHistoryEntity ah " +
           "JOIN ah.question.learningPathNode lpn WHERE lpn.objective.lesson.topic.id = :topicId")
    boolean existsAnswerHistoryByTopicId(@Param("topicId") Long topicId);

    @Query("SELECT CASE WHEN COUNT(ah) > 0 THEN true ELSE false END FROM AnswerHistoryEntity ah WHERE ah.question.id = :questionId")
    boolean existsAnswerHistoryByQuestionId(@Param("questionId") Long questionId);

    @Modifying
    @Query("DELETE FROM SpeakingQuestionEntity q WHERE q.id IN " +
           "(SELECT lpn.speakingQuestion.id FROM LearningPathNodeEntity lpn WHERE lpn.objective.lesson.topic.id = :topicId)")
    void deleteAllByTopicId(@Param("topicId") Long topicId);

    @Modifying
    @Query("UPDATE SpeakingQuestionEntity q SET q.status = :status WHERE q.id IN " +
           "(SELECT lpn.speakingQuestion.id FROM LearningPathNodeEntity lpn WHERE lpn.objective.lesson.topic.id = :topicId)")
    void updateStatusByTopicId(@Param("topicId") Long topicId, @Param("status") QuestionStatus status);
}
