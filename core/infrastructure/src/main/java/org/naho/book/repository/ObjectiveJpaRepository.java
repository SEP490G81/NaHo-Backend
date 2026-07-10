package org.naho.book.repository;

import org.naho.book.entity.ObjectiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjectiveJpaRepository extends JpaRepository<ObjectiveEntity, Long> {
}
