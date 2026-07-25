package org.naho.learning.repository;

import org.naho.learning.entity.UserLearningProgressEntity;
import org.naho.shared.persistence.BaseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLearningProgressJpaRepository extends BaseJpaRepository<UserLearningProgressEntity> {
    Optional<UserLearningProgressEntity> findByUserId(Long userId);

    boolean existsByUser_Id(Long userId);
}
