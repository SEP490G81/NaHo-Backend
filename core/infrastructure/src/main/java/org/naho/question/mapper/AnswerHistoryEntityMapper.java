package org.naho.question.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.file.entity.FileEntity;
import org.naho.question.entity.AnswerHistoryEntity;
import org.naho.question.entity.SpeakingQuestionEntity;
import org.naho.speech.azure.entity.ContentAssessmentEntity;
import org.naho.speech.azure.entity.SpeechAssessmentEntity;
import org.naho.speech.azure.entity.WordAssessmentEntity;
import org.naho.speech.llm.result.SpeakingHistoryListItemResult;
import org.naho.speech.model.AnswerHistory;
import org.naho.speech.model.ContentAssessment;
import org.naho.speech.model.SpeechAssessment;
import org.naho.speech.model.WordAssessment;
import org.naho.user.entity.UserEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnswerHistoryEntityMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "speakingQuestionId", source = "speakingQuestion.id")
    @Mapping(target = "audioFileId", source = "audioFile.id")
    AnswerHistory toDomain(AnswerHistoryEntity entity);

    @Mapping(target = "id", source = "domain.id")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "speakingQuestion", source = "question")
    @Mapping(target = "audioFile", source = "file")
    @Mapping(target = "contentAssessment", ignore = true)
    @Mapping(target = "speechAssessment", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    AnswerHistoryEntity toEntity(AnswerHistory domain, UserEntity user, SpeakingQuestionEntity question, FileEntity file);

    @Mapping(target = "answerHistoryId", source = "answerHistory.id")
    SpeechAssessment toDomain(SpeechAssessmentEntity entity);

    @Mapping(target = "id", source = "domain.id")
    @Mapping(target = "answerHistory", source = "answerHistory")
    @Mapping(target = "transcriptText", source = "domain.transcriptText")
    @Mapping(target = "accuracyScore", source = "domain.accuracyScore")
    @Mapping(target = "fluencyScore", source = "domain.fluencyScore")
    @Mapping(target = "completenessScore", source = "domain.completenessScore")
    @Mapping(target = "pronunciationScore", source = "domain.pronunciationScore")
    @Mapping(target = "words", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    SpeechAssessmentEntity toEntity(SpeechAssessment domain, AnswerHistoryEntity answerHistory);

    @Mapping(target = "answerHistoryId", source = "answerHistory.id")
    ContentAssessment toDomain(ContentAssessmentEntity entity);

    @Mapping(target = "id", source = "domain.id")
    @Mapping(target = "answerHistory", source = "answerHistory")
    @Mapping(target = "vocabularyScore", source = "domain.vocabularyScore")
    @Mapping(target = "grammarScore", source = "domain.grammarScore")
    @Mapping(target = "aiFeedback", source = "domain.aiFeedback")
    @Mapping(target = "translationText", source = "domain.translationText")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    ContentAssessmentEntity toEntity(ContentAssessment domain, AnswerHistoryEntity answerHistory);

    @Mapping(target = "speechAssessmentId", source = "speechAssessment.id")
    WordAssessment toDomain(WordAssessmentEntity entity);

    List<WordAssessment> toWordAssessmentDomainList(List<WordAssessmentEntity> entities);

    @Mapping(target = "id", source = "domain.id")
    @Mapping(target = "word", source = "domain.word")
    @Mapping(target = "accuracyScore", source = "domain.accuracyScore")
    @Mapping(target = "errorType", source = "domain.errorType")
    @Mapping(target = "speechAssessment", source = "speechAssessment")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    WordAssessmentEntity toEntity(WordAssessment domain, SpeechAssessmentEntity speechAssessment);

    @Mapping(target = "historyId", source = "entity.id")
    @Mapping(target = "speakingQuestionId", source = "entity.speakingQuestion.id")
    @Mapping(target = "speakingQuestionTitle", source = "entity.speakingQuestion.japaneseName")
    @Mapping(target = "topicId", source = "entity.speakingQuestion.learningPathNode.objective.lesson.topic.id")
    @Mapping(target = "topicName", source = "entity.speakingQuestion.learningPathNode.objective.lesson.topic.japaneseName")
    @Mapping(target = "learningPathNodeId", source = "entity.speakingQuestion.learningPathNode.id")
    @Mapping(target = "bookId", source = "entity.speakingQuestion.learningPathNode.objective.lesson.topic.book.id")
    @Mapping(target = "score", source = "score")
    @Mapping(target = "durationSec", source = "durationSec")
    @Mapping(target = "audioUrl", source = "audioUrl")
    @Mapping(target = "practicedAt", source = "entity.createdTime")
    SpeakingHistoryListItemResult toListItemResult(
            AnswerHistoryEntity entity,
            Double score,
            Integer durationSec,
            String audioUrl
    );
}
