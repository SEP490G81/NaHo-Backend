package org.naho.social.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.question.mapper.SpeakingQuestionIdMapper;
import org.naho.social.comment.model.Comment;
import org.naho.social.entity.CommentEntity;
import org.naho.user.mapper.UserIdMapper;

@Mapper(componentModel = "spring", uses = {
        UserIdMapper.class,
        SpeakingQuestionIdMapper.class,
        CommentIdMapper.class
})
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
}
