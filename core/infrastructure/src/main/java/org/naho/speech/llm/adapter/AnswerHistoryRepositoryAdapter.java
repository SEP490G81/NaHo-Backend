package org.naho.speech.llm.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.model.FileEntity;
import org.naho.file.repository.FileJpaRepository;
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
import org.naho.speech.llm.port.out.AnswerHistoryRepositoryPort;
import org.naho.speech.model.AnswerHistory;
import org.naho.speech.model.ContentAssessment;
import org.naho.speech.model.SpeechAssessment;
import org.naho.speech.model.WordAssessment;
import org.naho.user.entity.UserEntity;
import org.naho.user.repository.UserJpaRepository;
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

    @Override
    public AnswerHistory saveAnswerHistory(AnswerHistory domain) {
        UserEntity user = userJpaRepository.getReferenceById(domain.getUserId());
        SpeakingQuestionEntity question = questionJpaRepository.getReferenceById(domain.getQuestionId());
        FileEntity file = fileJpaRepository.getReferenceById(domain.getAudioFileId());

        AnswerHistoryEntity entity = AnswerHistoryEntity.builder()
                .id(domain.getId())
                .user(user)
                .question(question)
                .audioFile(file)
                .build();

        AnswerHistoryEntity saved = answerHistoryJpaRepository.save(entity);
        return AnswerHistory.builder()
                .id(saved.getId())
                .userId(saved.getUser().getId())
                .questionId(saved.getQuestion().getId())
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
                .questionId(entity.getQuestion().getId())
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
}
