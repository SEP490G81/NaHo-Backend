package org.naho.question.repository;

import org.naho.question.entity.QuestionEntity;
import org.naho.question.type.QuestionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionJpaRepository extends JpaRepository<QuestionEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(ah) > 0 THEN true ELSE false END FROM AnswerHistoryEntity ah WHERE ah.question.topic.id = :topicId")
    boolean existsAnswerHistoryByTopicId(@Param("topicId") Long topicId);

    @Query("SELECT CASE WHEN COUNT(ah) > 0 THEN true ELSE false END FROM AnswerHistoryEntity ah WHERE ah.question.id = :questionId")
    boolean existsAnswerHistoryByQuestionId(@Param("questionId") Long questionId);

    void deleteAllByTopicId(Long topicId);

    @Modifying
    @Query("UPDATE QuestionEntity q SET q.status = :status WHERE q.topic.id = :topicId")
    void updateStatusByTopicId(@Param("topicId") Long topicId, @Param("status") QuestionStatus status);

    @Query("SELECT MAX(q.orderIndex) FROM QuestionEntity q WHERE q.topic.id = :topicId")
    Double getMaxOrderIndexByTopicId(@Param("topicId") Long topicId);

    boolean existsByTopicIdAndTitle(Long topicId, String title);

    boolean existsByTopicIdAndTitleAndIdNot(Long topicId, String title, Long id);
}
