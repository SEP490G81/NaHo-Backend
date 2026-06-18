package org.naho.question.repository;

import org.naho.question.entity.QuestionEntity;
import org.naho.topic.type.QuestionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionJpaRepository extends JpaRepository<QuestionEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(ah) > 0 THEN true ELSE false END FROM AnswerHistoryEntity ah WHERE ah.question.topic.id = :topicId")
    boolean existsAnswerHistoryByTopicId(@Param("topicId") Long topicId);

    void deleteAllByTopicId(Long topicId);

    @Modifying
    @Query("UPDATE QuestionEntity q SET q.status = :status WHERE q.topic.id = :topicId")
    void updateStatusByTopicId(@Param("topicId") Long topicId, @Param("status") QuestionStatus status);
}
