package org.naho.file.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.file.entity.FileEntity;
import org.naho.file.model.File;
import org.naho.league.mapper.LeagueIdMapper;
import org.naho.persona.mapper.PersonaIdMapper;
import org.naho.question.mapper.SpeakingQuestionIdMapper;
import org.naho.social.comment.mapper.CommentIdMapper;
import org.naho.social.report.mapper.ReportIdMapper;
import org.naho.user.mapper.UserIdMapper;

@Mapper(componentModel = "spring", uses = {
        FileValueObjectMapper.class,
        CommentIdMapper.class,
        ReportIdMapper.class,
        UserIdMapper.class,
        PersonaIdMapper.class,
        LeagueIdMapper.class,
        SpeakingQuestionIdMapper.class
})
public interface FileEntityMapper {
    @Mapping(target = "commentId", source = "comment.id")
    @Mapping(target = "reportId", source = "report.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "personaId", source = "persona.id")
    @Mapping(target = "leagueId", source = "league.id")
    @Mapping(target = "speakingQuestionId", source = "speakingQuestion.id")
    File entityToDomain(FileEntity entity);

    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    @Mapping(target = "persona", source = "personaId")
    @Mapping(target = "comment", source = "commentId")
    @Mapping(target = "speakingQuestion", source = "speakingQuestionId")
    @Mapping(target = "report", source = "reportId")
    @Mapping(target = "league", source = "leagueId")
    @Mapping(target = "user", source = "userId")
    FileEntity domainToEntity(File domain);
}
