package org.naho.social.reaction.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.question.mapper.SpeakingQuestionIdMapper;
import org.naho.social.comment.mapper.CommentIdMapper;
import org.naho.social.entity.ReactionEntity;
import org.naho.social.reaction.model.Reaction;
import org.naho.user.mapper.UserIdMapper;

@Mapper(componentModel = "spring", uses = {
        UserIdMapper.class,
        SpeakingQuestionIdMapper.class,
        CommentIdMapper.class
})
public interface ReactionEntityMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "questionId", source = "question.id")
    @Mapping(target = "commentId", source = "comment.id")
    Reaction entityToDomain(ReactionEntity entity);

    @Mapping(target = "user", source = "userId")
    @Mapping(target = "question", source = "questionId")
    @Mapping(target = "comment", source = "commentId")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    ReactionEntity domainToEntity(Reaction domain);
}
