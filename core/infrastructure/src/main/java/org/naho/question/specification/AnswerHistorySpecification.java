package org.naho.question.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.naho.book.entity.LessonEntity;
import org.naho.book.entity.ObjectiveEntity;
import org.naho.book.entity.TopicEntity;
import org.naho.learning.entity.LearningPathNodeEntity;
import org.naho.question.entity.AnswerHistoryEntity;
import org.naho.question.entity.SpeakingQuestionEntity;
import org.springframework.data.jpa.domain.Specification;

public final class AnswerHistorySpecification {

    private AnswerHistorySpecification() {
    }

    public static Specification<AnswerHistoryEntity> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("user").get("id"), userId);
        };
    }

    public static Specification<AnswerHistoryEntity> hasSpeakingQuestionId(Long speakingQuestionId) {
        return (root, query, criteriaBuilder) -> {
            if (speakingQuestionId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("speakingQuestion").get("id"), speakingQuestionId);
        };
    }

    public static Specification<AnswerHistoryEntity> hasTopicId(Long topicId) {
        return (root, query, criteriaBuilder) -> {
            if (topicId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<AnswerHistoryEntity, SpeakingQuestionEntity> sq = root.join("speakingQuestion", JoinType.LEFT);
            Join<SpeakingQuestionEntity, LearningPathNodeEntity> lpn = sq.join("learningPathNode", JoinType.LEFT);
            Join<LearningPathNodeEntity, ObjectiveEntity> obj = lpn.join("objective", JoinType.LEFT);
            Join<ObjectiveEntity, LessonEntity> les = obj.join("lesson", JoinType.LEFT);
            Join<LessonEntity, TopicEntity> t = les.join("topic", JoinType.LEFT);

            return criteriaBuilder.equal(t.get("id"), topicId);
        };
    }

    public static Specification<AnswerHistoryEntity> searchByTitle(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            Join<AnswerHistoryEntity, SpeakingQuestionEntity> sq = root.join("speakingQuestion", JoinType.LEFT);
            return criteriaBuilder.like(
                    criteriaBuilder.lower(sq.get("title")),
                    "%" + search.trim().toLowerCase() + "%"
            );
        };
    }
}
