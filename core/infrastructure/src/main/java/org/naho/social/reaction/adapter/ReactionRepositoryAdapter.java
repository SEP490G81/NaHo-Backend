package org.naho.social.reaction.adapter;

import org.naho.social.comment.entity.ReactionEntity;
import org.naho.social.reaction.mapper.ReactionEntityMapper;
import org.naho.social.reaction.model.Reaction;
import org.naho.social.reaction.port.out.ReactionRepositoryPort;
import org.naho.social.reaction.repository.ReactionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReactionRepositoryAdapter implements ReactionRepositoryPort {

    private final ReactionJpaRepository reactionJpaRepository;
    private final ReactionEntityMapper reactionEntityMapper;

    public ReactionRepositoryAdapter(ReactionJpaRepository reactionJpaRepository,
                                     ReactionEntityMapper reactionEntityMapper) {
        this.reactionJpaRepository = reactionJpaRepository;
        this.reactionEntityMapper = reactionEntityMapper;
    }

    @Override
    public Reaction save(Reaction reaction) {
        ReactionEntity entity = reactionEntityMapper.domainToEntity(reaction);
        ReactionEntity savedEntity = reactionJpaRepository.save(entity);
        return reactionEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public Reaction findByUserAndTarget(Long userId, Long commentId) {
        return reactionJpaRepository.findByUserIdAndCommentId(userId, commentId)
                .map(reactionEntityMapper::entityToDomain)
                .orElse(null);
    }

    @Override
    public void delete(Long id) {
        reactionJpaRepository.deleteById(id);
    }

    @Override
    public List<Reaction> findByCommentId(Long commentId) {
        return reactionJpaRepository.findByCommentId(commentId)
                .stream()
                .map(reactionEntityMapper::entityToDomain)
                .toList();
    }
}
