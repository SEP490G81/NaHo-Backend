package org.naho.speech.llm.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.naho.book.entity.LessonEntity;
import org.naho.book.entity.ObjectiveEntity;
import org.naho.book.entity.TopicEntity;
import org.naho.file.entity.FileEntity;
import org.naho.file.repository.FileJpaRepository;
import org.naho.learning.entity.LearningPathNodeEntity;
import org.naho.point.constant.CloudFrontProperties;
import org.naho.question.entity.AnswerHistoryEntity;
import org.naho.question.entity.SpeakingQuestionEntity;
import org.naho.question.repository.AnswerHistoryJpaRepository;
import org.naho.question.repository.SpeakingQuestionJpaRepository;
import org.naho.speech.azure.entity.ContentAssessmentEntity;
import org.naho.speech.azure.entity.SpeechAssessmentEntity;
import org.naho.speech.azure.entity.WordAssessmentEntity;
import org.naho.speech.azure.repository.ContentAssessmentJpaRepository;
import org.naho.speech.azure.repository.SpeechAssessmentJpaRepository;
import org.naho.speech.azure.repository.WordAssessmentJpaRepository;
import org.naho.speech.llm.command.SpeakingHistoryFilterCommand;
import org.naho.speech.llm.port.out.AnswerHistoryRepositoryPort;
import org.naho.speech.llm.result.SpeakingHistoryListItemResult;
import org.naho.speech.llm.result.SpeakingHistoryListResult;
import org.naho.speech.model.AnswerHistory;
import org.naho.speech.model.ContentAssessment;
import org.naho.speech.model.SpeechAssessment;
import org.naho.speech.model.WordAssessment;
import org.naho.user.entity.UserEntity;
import org.naho.user.repository.UserJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AnswerHistoryRepositoryAdapter implements AnswerHistoryRepositoryPort {

    private final AnswerHistoryJpaRepository answerHistoryJpaRepository;
    private final SpeechAssessmentJpaRepository speechAssessmentJpaRepository;
    private final ContentAssessmentJpaRepository contentAssessmentJpaRepository;
    private final WordAssessmentJpaRepository wordAssessmentJpaRepository;

    private final UserJpaRepository userJpaRepository;
    private final SpeakingQuestionJpaRepository questionJpaRepository;
    private final FileJpaRepository fileJpaRepository;
    private final CloudFrontProperties cloudFrontProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AnswerHistory saveAnswerHistory(AnswerHistory domain) {
        UserEntity user = userJpaRepository.getReferenceById(domain.getUserId());
        SpeakingQuestionEntity question = questionJpaRepository.getReferenceById(domain.getSpeakingQuestionId());
        FileEntity file = fileJpaRepository.getReferenceById(domain.getAudioFileId());

        AnswerHistoryEntity entity = AnswerHistoryEntity.builder()
                .id(domain.getId())
                .user(user)
                .speakingQuestion(question)
                .audioFile(file)
                .build();

        AnswerHistoryEntity saved = answerHistoryJpaRepository.save(entity);
        return AnswerHistory.builder()
                .id(saved.getId())
                .userId(saved.getUser().getId())
                .speakingQuestionId(saved.getSpeakingQuestion().getId())
                .audioFileId(saved.getAudioFile().getId())
                .createdTime(saved.getCreatedTime())
                .build();
    }

    @Override
    public SpeechAssessment saveSpeechAssessment(SpeechAssessment domain) {
        AnswerHistoryEntity answerHistory = answerHistoryJpaRepository.getReferenceById(domain.getAnswerHistoryId());

        SpeechAssessmentEntity entity = SpeechAssessmentEntity.builder()
                .id(domain.getId())
                .transcriptText(domain.getTranscriptText())
                .accuracyScore(domain.getAccuracyScore())
                .fluencyScore(domain.getFluencyScore())
                .completenessScore(domain.getCompletenessScore())
                .pronunciationScore(domain.getPronunciationScore())
                .answerHistory(answerHistory)
                .build();

        SpeechAssessmentEntity saved = speechAssessmentJpaRepository.save(entity);
        return SpeechAssessment.builder()
                .id(saved.getId())
                .transcriptText(saved.getTranscriptText())
                .accuracyScore(saved.getAccuracyScore())
                .fluencyScore(saved.getFluencyScore())
                .completenessScore(saved.getCompletenessScore())
                .pronunciationScore(saved.getPronunciationScore())
                .answerHistoryId(saved.getAnswerHistory().getId())
                .build();
    }

    @Override
    public ContentAssessment saveContentAssessment(ContentAssessment domain) {
        AnswerHistoryEntity answerHistory = answerHistoryJpaRepository.getReferenceById(domain.getAnswerHistoryId());

        ContentAssessmentEntity entity = ContentAssessmentEntity.builder()
                .id(domain.getId())
                .vocabularyScore(domain.getVocabularyScore())
                .grammarScore(domain.getGrammarScore())
                .aiFeedback(domain.getAiFeedback())
                .translationText(domain.getTranslationText())
                .answerHistory(answerHistory)
                .build();

        ContentAssessmentEntity saved = contentAssessmentJpaRepository.save(entity);
        return ContentAssessment.builder()
                .id(saved.getId())
                .vocabularyScore(saved.getVocabularyScore())
                .grammarScore(saved.getGrammarScore())
                .aiFeedback(saved.getAiFeedback())
                .translationText(saved.getTranslationText())
                .answerHistoryId(saved.getAnswerHistory().getId())
                .build();
    }

    @Override
    public List<WordAssessment> saveAllWordAssessment(List<WordAssessment> domains) {
        List<WordAssessmentEntity> entities = domains.stream().map(domain -> {
            SpeechAssessmentEntity sa = speechAssessmentJpaRepository.getReferenceById(domain.getSpeechAssessmentId());
            WordAssessmentEntity entity = WordAssessmentEntity.builder()
                    .word(domain.getWord())
                    .accuracyScore(domain.getAccuracyScore())
                    .errorType(domain.getErrorType())
                    .speechAssessment(sa)
                    .build();
            return entity;
        }).toList();

        List<WordAssessmentEntity> saved = wordAssessmentJpaRepository.saveAll(entities);
        return saved.stream().map(e -> WordAssessment.builder()
                .id(e.getId())
                .word(e.getWord())
                .accuracyScore(e.getAccuracyScore())
                .errorType(e.getErrorType())
                .speechAssessmentId(e.getSpeechAssessment().getId())
                .build()).toList();
    }

    @Override
    public Optional<AnswerHistory> findAnswerHistoryById(Long id) {
        return answerHistoryJpaRepository.findById(id).map(entity -> AnswerHistory.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .speakingQuestionId(entity.getSpeakingQuestion().getId())
                .audioFileId(entity.getAudioFile().getId())
                .createdTime(entity.getCreatedTime())
                .build());
    }

    @Override
    public Optional<SpeechAssessment> findSpeechAssessmentByAnswerHistoryId(Long answerHistoryId) {
        return speechAssessmentJpaRepository.findByAnswerHistoryId(answerHistoryId).map(entity -> {
            List<WordAssessment> words = entity.getWords() != null ? entity.getWords().stream().map(w -> WordAssessment.builder()
                    .id(w.getId())
                    .word(w.getWord())
                    .accuracyScore(w.getAccuracyScore())
                    .errorType(w.getErrorType())
                    .speechAssessmentId(w.getSpeechAssessment().getId())
                    .build()).toList() : List.of();

            return SpeechAssessment.builder()
                    .id(entity.getId())
                    .transcriptText(entity.getTranscriptText())
                    .accuracyScore(entity.getAccuracyScore())
                    .fluencyScore(entity.getFluencyScore())
                    .completenessScore(entity.getCompletenessScore())
                    .pronunciationScore(entity.getPronunciationScore())
                    .answerHistoryId(entity.getAnswerHistory().getId())
                    .words(words)
                    .build();
        });
    }

    @Override
    public Optional<ContentAssessment> findContentAssessmentByAnswerHistoryId(Long answerHistoryId) {
        return contentAssessmentJpaRepository.findByAnswerHistoryId(answerHistoryId).map(entity -> ContentAssessment.builder()
                .id(entity.getId())
                .vocabularyScore(entity.getVocabularyScore())
                .grammarScore(entity.getGrammarScore())
                .aiFeedback(entity.getAiFeedback())
                .translationText(entity.getTranslationText())
                .answerHistoryId(entity.getAnswerHistory().getId())
                .build());
    }

    @Override
    public SpeakingHistoryListResult findUserAnswerHistories(SpeakingHistoryFilterCommand command) {
        Long userId = command != null ? command.userId() : null;
        Long questionId = command != null ? command.speakingQuestionId() : null;
        Long topicId = command != null ? command.topicId() : null;
        String search = command != null ? command.search() : null;

        int pageNumber = (command != null && command.page() > 0) ? command.page() - 1 : 0;
        int pageSize = (command != null && command.size() > 0) ? command.size() : 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdTime"));

        Page<AnswerHistoryEntity> entityPage = answerHistoryJpaRepository.findByUserIdAndFilters(userId, questionId, topicId, search, pageable);

        List<SpeakingHistoryListItemResult> items = entityPage.getContent().stream().map(entity -> {
            SpeakingQuestionEntity sq = entity.getSpeakingQuestion();
            Long sqId = sq != null ? sq.getId() : null;
            String sqTitle = sq != null ? sq.getTitle() : null;

            LearningPathNodeEntity lpn = sq != null ? sq.getLearningPathNode() : null;
            ObjectiveEntity obj = lpn != null ? lpn.getObjective() : null;
            LessonEntity les = obj != null ? obj.getLesson() : null;
            TopicEntity t = les != null ? les.getTopic() : null;

            Long tId = t != null ? t.getId() : null;
            String tName = t != null ? t.getJapaneseName() : null;

            String audioUrl = null;
            if (entity.getAudioFile() != null && entity.getAudioFile().getObjectKey() != null) {
                audioUrl = cloudFrontProperties.getDomain() + entity.getAudioFile().getObjectKey();
            }

            Double score = 0.0;
            Integer durationSec = 0;
            ContentAssessmentEntity ca = entity.getContentAssessment();
            if (ca != null && ca.getAiFeedback() != null && !ca.getAiFeedback().isBlank()) {
                try {
                    JsonNode root = objectMapper.readTree(ca.getAiFeedback());
                    score = root.path("overallScore").asDouble(0.0);
                    durationSec = root.path("durationSec").asInt(0);
                } catch (Exception ignored) {
                }
            }

            return new SpeakingHistoryListItemResult(
                    entity.getId(),
                    sqId,
                    sqTitle,
                    tId,
                    tName,
                    score,
                    durationSec,
                    audioUrl,
                    entity.getCreatedTime()
            );
        }).toList();

        return new SpeakingHistoryListResult(
                items,
                entityPage.getNumber() + 1,
                entityPage.getSize(),
                entityPage.getTotalPages(),
                entityPage.getTotalElements()
        );
    }
}
