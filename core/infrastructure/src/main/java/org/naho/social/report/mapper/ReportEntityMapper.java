package org.naho.social.report.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.question.mapper.SpeakingQuestionIdMapper;
import org.naho.social.comment.mapper.CommentIdMapper;
import org.naho.social.report.entity.ReportEntity;
import org.naho.social.report.model.Report;
import org.naho.user.mapper.UserIdMapper;

@Mapper(componentModel = "spring", uses = {
        UserIdMapper.class,
        SpeakingQuestionIdMapper.class,
        CommentIdMapper.class
})
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
    @Mapping(target = "files", ignore = true)
    ReportEntity domainToEntity(Report report);
}
