package org.naho.social.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.question.entity.SpeakingQuestionEntity;
import org.naho.social.comment.model.Comment;
import org.naho.social.entity.CommentEntity;
import org.naho.user.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface CommentEntityMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "questionId", source = "question.id")
    @Mapping(target = "parentId", source = "parent.id")
    Comment entityToDomain(CommentEntity entity);

    @Mapping(target = "user", source = "userId")
    @Mapping(target = "question", source = "questionId")
    @Mapping(target = "parent", source = "parentId")
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "reactions", ignore = true)
    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "files", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    CommentEntity domainToEntity(Comment domain);

    default UserEntity mapUserIdToUserEntity(Long userId) {
        if (userId == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setId(userId);
        return entity;
    }

    default SpeakingQuestionEntity mapQuestionIdToQuestionEntity(Long questionId) {
        if (questionId == null) {
            return null;
        }
        SpeakingQuestionEntity entity = new SpeakingQuestionEntity();
        entity.setId(questionId);
        return entity;
    }

    default CommentEntity mapParentIdToCommentEntity(Long parentId) {
        if (parentId == null) {
            return null;
        }
        CommentEntity entity = new CommentEntity();
        entity.setId(parentId);
        return entity;
    }
}
