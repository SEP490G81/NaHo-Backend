package org.naho.social.reaction.repository;

import org.naho.social.comment.entity.ReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionJpaRepository extends JpaRepository<ReactionEntity, Long> {
    Optional<ReactionEntity> findByUserIdAndCommentId(Long userId, Long commentId);

    List<ReactionEntity> findByCommentId(Long commentId);
}
