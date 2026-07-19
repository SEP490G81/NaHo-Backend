package org.naho.question.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.repository.FileJpaRepository;
import org.naho.question.entity.SpeakingQuestionEntity;
import org.naho.question.model.Grammar;
import org.naho.question.model.SpeakingQuestion;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.repository.SpeakingQuestionJpaRepository;
import org.naho.question.type.QuestionStatus;
import org.naho.user.repository.UserJpaRepository;
import org.naho.vocabulary.model.Vocabulary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SpeakingQuestionRepositoryAdapter implements SpeakingQuestionRepositoryPort {

    private final SpeakingQuestionJpaRepository speakingQuestionJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final FileJpaRepository fileJpaRepository;

    @Override
    public void deleteSpeakingQuestionsByTopicId(Long topicId) {
        speakingQuestionJpaRepository.deleteAllByTopicId(topicId);
    }

    @Override
    public void updateSpeakingQuestionsStatusByTopicId(Long topicId, QuestionStatus status) {
        speakingQuestionJpaRepository.updateStatusByTopicId(topicId, status);
    }

    @Override
    public boolean hasAnySpeakingQuestionBeenAnsweredInTopic(Long topicId) {
        return speakingQuestionJpaRepository.existsAnswerHistoryByTopicId(topicId);
    }

    @Override
    public boolean hasSpeakingQuestionBeenAnswered(Long questionId) {
        return speakingQuestionJpaRepository.existsAnswerHistoryByQuestionId(questionId);
    }


    @Override
    public void deleteById(Long id) {
        speakingQuestionJpaRepository.deleteById(id);
    }

    @Override
    public SpeakingQuestion save(SpeakingQuestion speakingQuestion) {
        SpeakingQuestionEntity entity = new SpeakingQuestionEntity();

        if (speakingQuestion.getId() != null) {
            entity.setId(speakingQuestion.getId());
        }

        entity.setTitle(speakingQuestion.getTitle());
        entity.setTitleMarkup(speakingQuestion.getTitleMarkup());
        entity.setDescription(speakingQuestion.getDescription());
        entity.setDescriptionMarkup(speakingQuestion.getDescriptionMarkup());
        entity.setStatus(speakingQuestion.getStatus());


        if (speakingQuestion.getUserId() != null) {
            entity.setUser(userJpaRepository.getReferenceById(speakingQuestion.getUserId()));
        }

        if (speakingQuestion.getQuestionAudioFileId() != null) {
            entity.setQuestionAudioFile(fileJpaRepository.getReferenceById(speakingQuestion.getQuestionAudioFileId()));
        }

        SpeakingQuestionEntity savedEntity = speakingQuestionJpaRepository.save(entity);

        return SpeakingQuestion.builder()
                .id(savedEntity.getId())
                .questionAudioFileId(savedEntity.getQuestionAudioFile() != null ? savedEntity.getQuestionAudioFile().getId() : null)

                .userId(savedEntity.getUser() != null ? savedEntity.getUser().getId() : null)
                .title(savedEntity.getTitle())
                .titleMarkup(savedEntity.getTitleMarkup())
                .description(savedEntity.getDescription())
                .descriptionMarkup(savedEntity.getDescriptionMarkup())
                .status(savedEntity.getStatus())
                .vocabularies(mapVocabularies(savedEntity))
                .grammars(mapGrammars(savedEntity))
                .build();
    }

    @Override
    public Optional<SpeakingQuestion> findById(Long id) {
        return speakingQuestionJpaRepository.findById(id).map(entity -> SpeakingQuestion.builder()
                .id(entity.getId())
                .questionAudioFileId(entity.getQuestionAudioFile() != null ? entity.getQuestionAudioFile().getId() : null)

                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .title(entity.getTitle())
                .titleMarkup(entity.getTitleMarkup())
                .description(entity.getDescription())
                .descriptionMarkup(entity.getDescriptionMarkup())
                .status(entity.getStatus())
                .vocabularies(mapVocabularies(entity))
                .grammars(mapGrammars(entity))
                .build());
    }

    @Override
    public List<SpeakingQuestion> findByObjectiveId(Long objectiveId) {
        return speakingQuestionJpaRepository.findByObjectiveId(objectiveId).stream()
                .map(entity -> SpeakingQuestion.builder()
                        .id(entity.getId())
                        .questionAudioFileId(entity.getQuestionAudioFile() != null ? entity.getQuestionAudioFile().getId() : null)
                        .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                        .title(entity.getTitle())
                        .titleMarkup(entity.getTitleMarkup())
                        .description(entity.getDescription())
                        .descriptionMarkup(entity.getDescriptionMarkup())
                        .status(entity.getStatus())
                        .orderIndex(entity.getLearningPathNode() != null ? entity.getLearningPathNode().getOrderIndex() : null)
                        .objectiveId(entity.getLearningPathNode() != null && entity.getLearningPathNode().getObjective() != null ? entity.getLearningPathNode().getObjective().getId() : null)
                        .vocabularies(mapVocabularies(entity))
                        .grammars(mapGrammars(entity))
                        .build())
                .toList();
    }

    private List<Vocabulary> mapVocabularies(SpeakingQuestionEntity entity) {
        if (entity.getVocabularies() == null) {
            return List.of();
        }
        return entity.getVocabularies().stream()
                .map(sqv -> Vocabulary.builder()
                        .id(sqv.getVocabulary().getId())
                        .reading(sqv.getVocabulary().getReading())
                        .japanese(sqv.getVocabulary().getJapanese())
                        .vietnameseMeaningText(sqv.getVocabulary().getVietnameseMeaningText())
                        .englishMeaningText(sqv.getVocabulary().getEnglishMeaningText())
                        .build())
                .toList();
    }

    private List<Grammar> mapGrammars(SpeakingQuestionEntity entity) {
        if (entity.getGrammars() == null) {
            return List.of();
        }
        return entity.getGrammars().stream()
                .map(sqg -> Grammar.builder()
                        .id(sqg.getGrammar().getId())
                        .reading(sqg.getGrammar().getReading())
                        .japanese(sqg.getGrammar().getJapanese())
                        .vietnameseMeaningText(sqg.getGrammar().getVietnameseMeaningText())
                        .englishMeaningText(sqg.getGrammar().getEnglishMeaningText())
                        .build())
                .toList();
    }
}
