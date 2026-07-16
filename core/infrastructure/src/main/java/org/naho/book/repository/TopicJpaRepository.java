package org.naho.book.repository;

import org.naho.book.entity.TopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicJpaRepository extends JpaRepository<TopicEntity, Long> {
    boolean existsByJapaneseNameAndBookId(String japaneseName, Long bookId);

    boolean existsByJapaneseNameAndBookIdAndIdNot(String japaneseName, Long bookId, Long id);

    @Query("SELECT MAX(t.orderIndex) FROM TopicEntity t")
    Double getMaxOrderIndex();

    @Query("SELECT t FROM TopicEntity t JOIN t.lessons l JOIN l.objectives o WHERE o.id = :objectiveId")
    Optional<TopicEntity> findByObjectiveId(@Param("objectiveId") Long objectiveId);

    List<TopicEntity> findAllByBook_Id(Long bookId);
}
