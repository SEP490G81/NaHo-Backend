package org.naho.social.report.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.question.entity.SpeakingQuestionEntity;
import org.naho.social.entity.CommentEntity;
import org.naho.social.model.Report;
import org.naho.social.report.entity.ReportEntity;
import org.naho.user.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface ReportEntityMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "questionId", source = "question.id")
    @Mapping(target = "commentId", source = "comment.id")
    Report entityToDomain(ReportEntity entity);

    @Mapping(target = "user", source = "userId")
    @Mapping(target = "question", source = "questionId")
    @Mapping(target = "comment", source = "commentId")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    @Mapping(target = "isResolved", source = "resolved")
    ReportEntity domainToEntity(Report report);

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

    default CommentEntity mapCommentIdToCommentEntity(Long commentId) {
        if (commentId == null) {
            return null;
        }
        CommentEntity entity = new CommentEntity();
        entity.setId(commentId);
        return entity;
    }
}
