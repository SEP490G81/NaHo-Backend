package org.naho.social.comment.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.social.comment.mapper.CommentEntityMapper;
import org.naho.social.comment.model.Comment;
import org.naho.social.comment.port.out.CommentRepositoryPort;
import org.naho.social.comment.repository.CommentJpaRepository;
import org.naho.social.entity.CommentEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryAdapter implements CommentRepositoryPort {

    private final CommentJpaRepository commentJpaRepository;
    private final CommentEntityMapper commentEntityMapper;

    @Override
    public List<Comment> getListCommentByQuestionId(Long speakingQuestionId) {
        return commentJpaRepository.findByQuestionId(speakingQuestionId)
                .stream()
                .map(commentEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public Optional<Comment> findByCommentId(Long commentId) {
        return commentJpaRepository.findById(commentId)
                .map(commentEntityMapper::entityToDomain);
    }

    @Override
    public Comment save(Comment comment) {
        CommentEntity entity = commentEntityMapper.domainToEntity(comment);
        CommentEntity savedEntity = commentJpaRepository.save(entity);
        return commentEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public void delete(Comment comment) {
        if (comment.getId() != null) {
            commentJpaRepository.deleteById(comment.getId());
        }
    }
}
