package org.naho.speech.llm.specification;

import org.naho.speech.llm.entity.SpeakingSessionEntity;
import org.springframework.data.jpa.domain.Specification;

public final class SpeakingSessionSpecification {

    private SpeakingSessionSpecification() {
    }

    public static Specification<SpeakingSessionEntity> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("userId"), userId);
        };
    }

    public static Specification<SpeakingSessionEntity> hasPersonaId(Long personaId) {
        return (root, query, criteriaBuilder) -> {
            if (personaId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("personaId"), personaId);
        };
    }

    public static Specification<SpeakingSessionEntity> searchByTopic(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("topic")),
                    "%" + search.trim().toLowerCase() + "%"
            );
        };
    }
}
