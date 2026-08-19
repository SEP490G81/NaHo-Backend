package org.naho.learning.usecase;


import org.naho.chest.exception.ChestErrorCode;
import org.naho.chest.mapper.ChestResultMapper;
import org.naho.chest.model.Chest;
import org.naho.chest.port.out.ChestRepositoryPort;
import org.naho.chest.result.ChestResult;
import org.naho.i18n.message.chest.ChestDetailMessageKey;
import org.naho.i18n.message.learning.LearningPathNodeDetailMessageKey;
import org.naho.i18n.message.question.VocabularyQuestionDetailMessageKey;
import org.naho.learning.command.GetLearningPathNodeDetailCommand;
import org.naho.learning.exception.LearningPathNodeErrorCode;
import org.naho.learning.model.LearningPathNode;
import org.naho.learning.port.in.GetLearningPathNodeDetailInputPort;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.result.LearningPathNodeDetailResult;
import org.naho.learning.type.NodeType;
import org.naho.question.command.FindSpeakingQuestionCommand;
import org.naho.question.exception.VocabularyQuestionErrorCode;
import org.naho.question.mapper.SpeakingQuestionResultMapper;
import org.naho.question.port.in.GetSpeakingQuestionInputPort;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.port.out.VocabularyQuestionRepositoryPort;
import org.naho.question.result.SpeakingQuestionResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.vocabulary.mapper.VocabularyResultMapper;
import org.naho.vocabulary.result.VocabularyQuestionResult;

public class GetLearningPathNodeDetailUseCase implements GetLearningPathNodeDetailInputPort {
    private final LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;
    private final SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort;
    private final VocabularyQuestionRepositoryPort vocabularyQuestionRepositoryPort;
    private final ChestRepositoryPort chestRepositoryPort;
    private final GetActiveSubscriptionInputPort getActiveSubscriptionInputPort;
    private final SpeakingQuestionResultMapper speakingQuestionResultMapper;
    private final VocabularyResultMapper vocabularyResultMapper;
    private final ChestResultMapper chestResultMapper;
    private final GetSpeakingQuestionInputPort getSpeakingQuestionInputPort;

    public GetLearningPathNodeDetailUseCase(
            LearningPathNodeRepositoryPort learningPathNodeRepositoryPort,
            SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort,
            VocabularyQuestionRepositoryPort vocabularyQuestionRepositoryPort,
            ChestRepositoryPort chestRepositoryPort,
            GetActiveSubscriptionInputPort getActiveSubscriptionInputPort,
            SpeakingQuestionResultMapper speakingQuestionResultMapper,
            VocabularyResultMapper vocabularyResultMapper,
            ChestResultMapper chestResultMapper,
            GetSpeakingQuestionInputPort getSpeakingQuestionInputPort
    ) {
        this.learningPathNodeRepositoryPort = learningPathNodeRepositoryPort;
        this.speakingQuestionRepositoryPort = speakingQuestionRepositoryPort;
        this.vocabularyQuestionRepositoryPort = vocabularyQuestionRepositoryPort;
        this.chestRepositoryPort = chestRepositoryPort;
        this.getActiveSubscriptionInputPort = getActiveSubscriptionInputPort;
        this.speakingQuestionResultMapper = speakingQuestionResultMapper;
        this.vocabularyResultMapper = vocabularyResultMapper;
        this.chestResultMapper = chestResultMapper;
        this.getSpeakingQuestionInputPort = getSpeakingQuestionInputPort;
    }

    @Override
    public LearningPathNodeDetailResult getLearningPathNodeDetail(GetLearningPathNodeDetailCommand command) {
        LearningPathNode node = learningPathNodeRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ApplicationException(
                        LearningPathNodeErrorCode.LEARNING_PATH_NODE_NOT_FOUND,
                        LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_ID_NOT_FOUND,
                        command.id()
                ));

        SpeakingQuestionResult speakingQuestionResult = null;
        VocabularyQuestionResult vocabularyQuestionResult = null;
        ChestResult chestResult = null;

        NodeType nodeType = node.getNodeType();

        if (nodeType.equals(NodeType.SPEAKING_QUESTION)
                && node.getSpeakingQuestionId() != null) {
            speakingQuestionResult = getSpeakingQuestionInputPort
                    .findById(new FindSpeakingQuestionCommand(
                            node.getSpeakingQuestionId(),
                            command.userId()
                    ));

        } else if (nodeType.equals(NodeType.VOCABULARY_QUESTION)
                && node.getVocabularyQuestionId() != null) {
            var vq = vocabularyQuestionRepositoryPort
                    .findById(node.getVocabularyQuestionId())
                    .orElseThrow(() -> new ApplicationException(
                            VocabularyQuestionErrorCode.VOCABULARY_QUESTION_NOT_FOUND,
                            VocabularyQuestionDetailMessageKey.VOCABULARY_QUESTION_NOT_FOUND,
                            node.getVocabularyQuestionId()
                    ));

            var vocabList = vq.getVocabularies().stream()
                    .map(vocabularyResultMapper::domainToResult)
                    .toList();

            vocabularyQuestionResult = new VocabularyQuestionResult(
                    vq.getId(),
                    vocabList
            );

        } else if (nodeType.equals(NodeType.CHEST)
                && node.getChestId() != null) {
            Chest chest = chestRepositoryPort.findById(node.getChestId())
                    .orElseThrow(() -> new ApplicationException(
                            ChestErrorCode.CHEST_NOT_FOUND,
                            ChestDetailMessageKey.CHEST_NOT_FOUND,
                            node.getChestId()
                    ));

            chestResult = chestResultMapper.domainToResult(chest);
        }

        return new LearningPathNodeDetailResult(
                node.getId(),
                node.getObjectiveId(),
                node.getNodeType(),
                node.getGlobalOrderIndex(),
                node.getOrderIndex(),
                speakingQuestionResult,
                vocabularyQuestionResult,
                chestResult
        );
    }
}
