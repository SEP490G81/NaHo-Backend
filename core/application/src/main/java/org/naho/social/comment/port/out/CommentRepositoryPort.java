package org.naho.social.comment.port.out;

import org.naho.social.comment.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryPort {
    List<Comment> getListCommentByQuestionId(Long speakingQuestionId);

    Optional<Comment> findByCommentId(Long commentId);

    Comment save(Comment comment);

    void delete(Comment comment);
}
