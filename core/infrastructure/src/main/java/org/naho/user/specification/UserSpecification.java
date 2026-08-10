package org.naho.user.specification;

import org.naho.user.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecification {
    private UserSpecification() {
    }

    // filter by username
    public static Specification<UserEntity> hasUsername(String username) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("username"), username);
    }

    // filter by email
    public static Specification<UserEntity> hasEmail(String email) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("email"), email);
    }

    // filter by full name
    public static Specification<UserEntity> hasFullName(String fullName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("fullName"), fullName);
    }
}
