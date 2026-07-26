package org.naho.question.repository;

import org.naho.question.entity.AnswerHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerHistoryJpaRepository extends JpaRepository<AnswerHistoryEntity, Long> {

    @Query("""
                SELECT ah FROM AnswerHistoryEntity ah
                LEFT JOIN ah.speakingQuestion sq
                LEFT JOIN sq.learningPathNode lpn
                LEFT JOIN lpn.objective obj
                LEFT JOIN obj.lesson les
                LEFT JOIN les.topic t
                WHERE ah.user.id = :userId
                  AND (:speakingQuestionId IS NULL OR sq.id = :speakingQuestionId)
                  AND (:topicId IS NULL OR t.id = :topicId)
                  AND (:search IS NULL OR :search = '' OR LOWER(sq.title) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<AnswerHistoryEntity> findByUserIdAndFilters(
            @Param("userId") Long userId,
            @Param("speakingQuestionId") Long speakingQuestionId,
            @Param("topicId") Long topicId,
            @Param("search") String search,
            Pageable pageable
    );
}
