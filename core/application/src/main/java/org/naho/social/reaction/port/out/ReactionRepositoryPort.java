package org.naho.social.reaction.port.out;

import org.naho.social.reaction.model.Reaction;

import java.util.List;

public interface ReactionRepositoryPort {
    Reaction save(Reaction reaction);

    Reaction findByUserAndTarget(Long userId, Long commentId);

    void delete(Long id);

    List<Reaction> findByCommentId(Long commentId);
}
