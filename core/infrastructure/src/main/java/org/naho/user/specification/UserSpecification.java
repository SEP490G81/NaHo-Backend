package org.naho.user.specification;

import jakarta.persistence.criteria.Path;
import org.naho.shared.specification.SpecificationHelper;
import org.naho.user.entity.UserEntity;
import org.naho.user.type.Gender;
import org.naho.user.type.UserStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class UserSpecification {
    private UserSpecification() {
    }

    // filter by username (ignore case)
    // if null skip this filter
    public static Specification<UserEntity> hasUsername(String username) {
        return ((root, query, criteriaBuilder) -> {
            if (username == null) {
                return criteriaBuilder.conjunction();
            }
            String pattern =
                    "%" + SpecificationHelper.escapeLikePattern(username.toLowerCase()) + "%";

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("username")),
                    pattern,
                    '\\' // để db hiểu rằng \% là kí tự % thực sự chứ không phải là pattern
            );
        });
    }

    // filter by email (ignore case)
    // if null skip this filter
    public static Specification<UserEntity> hasEmail(String email) {
        return ((root, query, criteriaBuilder) -> {
            if (email == null) {
                return criteriaBuilder.conjunction();
            }
            String pattern =
                    "%" + SpecificationHelper.escapeLikePattern(email.toLowerCase()) + "%";

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("email")),
                    pattern,
                    '\\'
            );
        });
    }

    // filter by full name (ignore case)
    // if null skip this filter
    public static Specification<UserEntity> hasFullName(String fullName) {
        return ((root, query, criteriaBuilder) -> {
            if (fullName == null) {
                return criteriaBuilder.conjunction();
            }
            String pattern =
                    "%" + SpecificationHelper.escapeLikePattern(fullName.toLowerCase()) + "%";

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("fullName")),
                    pattern,
                    '\\'
            );
        });
    }

    // filter by gender
    // if null skip this filter
    public static Specification<UserEntity> hasGender(Gender gender) {
        return ((root, query, criteriaBuilder) -> {
            if (gender == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("gender"), gender);
        });
    }

    // filter by date of birth
    // if both from and to are null skip this filter
    public static Specification<UserEntity> dobBetween(LocalDate from, LocalDate to) {
        return ((root, query, criteriaBuilder) -> {
            Path<LocalDate> dob = root.get("dob");

            if (from == null && to == null) {
                return criteriaBuilder.conjunction();
            }

            if (from != null && to == null) {
                return criteriaBuilder.greaterThanOrEqualTo(dob, from);
            }

            if (from == null) {
                return criteriaBuilder.lessThanOrEqualTo(dob, to);
            }

            return criteriaBuilder.between(dob, from, to);
        });
    }

    // filter by status
    // if null skip this filter
    public static Specification<UserEntity> hasStatus(UserStatus status) {
        return ((root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        });
    }

    // filter by role id
    // if null skip this filter
    public static Specification<UserEntity> hasRoleId(Long roleId) {
        return ((root, query, criteriaBuilder) -> {
            if (roleId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("role").get("id"), roleId);
        });
    }

    // filter by email verified
    // if null skip this filter
    public static Specification<UserEntity> isEmailVerified(Boolean isEmailVerified) {
        return ((root, query, criteriaBuilder) -> {
            if (isEmailVerified == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("isEmailVerified"), isEmailVerified);
        });
    }
}
