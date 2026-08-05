package org.naho.learning.usecase;


import org.naho.chest.exception.ChestErrorCode;
import org.naho.chest.port.out.ChestRepositoryPort;
import org.naho.chest.result.ChestResult;
import org.naho.grammar.result.GrammarDetailResult;
import org.naho.i18n.message.chest.ChestDetailMessageKey;
import org.naho.i18n.message.learning.LearningPathNodeDetailMessageKey;
import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.i18n.message.question.VocabularyQuestionDetailMessageKey;
import org.naho.learning.command.GetLearningPathNodeDetailCommand;
import org.naho.learning.exception.LearningPathNodeErrorCode;
import org.naho.learning.model.LearningPathNode;
import org.naho.learning.port.in.GetLearningPathNodeDetailInputPort;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.result.LearningPathNodeDetailResult;
import org.naho.learning.type.NodeType;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.question.exception.VocabularyQuestionErrorCode;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.port.out.VocabularyQuestionRepositoryPort;
import org.naho.question.result.SpeakingQuestionDetailResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.vocabulary.result.VocabularyDetailResult;
import org.naho.vocabulary.result.VocabularyQuestionDetailResult;

import java.util.List;

public class GetLearningPathNodeDetailUseCase implements GetLearningPathNodeDetailInputPort {
    private final LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;
    private final SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort;
    private final VocabularyQuestionRepositoryPort vocabularyQuestionRepositoryPort;
    private final ChestRepositoryPort chestRepositoryPort;

    public GetLearningPathNodeDetailUseCase(
            LearningPathNodeRepositoryPort learningPathNodeRepositoryPort,
            SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort,
            VocabularyQuestionRepositoryPort vocabularyQuestionRepositoryPort,
            ChestRepositoryPort chestRepositoryPort
    ) {
        this.learningPathNodeRepositoryPort = learningPathNodeRepositoryPort;
        this.speakingQuestionRepositoryPort = speakingQuestionRepositoryPort;
        this.vocabularyQuestionRepositoryPort = vocabularyQuestionRepositoryPort;
        this.chestRepositoryPort = chestRepositoryPort;
    }

    @Override
    public LearningPathNodeDetailResult getLearningPathNodeDetail(GetLearningPathNodeDetailCommand command) {
        LearningPathNode node = learningPathNodeRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ApplicationException(
                        LearningPathNodeErrorCode.LEARNING_PATH_NODE_NOT_FOUND,
                        LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_ID_NOT_FOUND,
                        command.id()
                ));

        SpeakingQuestionDetailResult speakingQuestionResult = null;
        VocabularyQuestionDetailResult vocabularyQuestionResult = null;
        ChestResult chestResult = null;

        NodeType nodeType = node.getNodeType();

        if (nodeType.equals(NodeType.SPEAKING_QUESTION)
                && node.getSpeakingQuestionId() != null) {
            var sq = speakingQuestionRepositoryPort.findById(node.getSpeakingQuestionId())
                    .orElseThrow(() -> new ApplicationException(
                            SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                            SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_NOT_FOUND,
                            node.getSpeakingQuestionId())
                    );

            var vocabList = sq.getVocabularies() == null ? List.<VocabularyDetailResult>of() : sq.getVocabularies().stream()
                    .map(v -> new VocabularyDetailResult(
                            v.getId(),
                            v.getReading(),
                            v.getJapanese(),
                            v.getVietnameseMeaningText(),
                            v.getEnglishMeaningText()))
                    .toList();
            var grammarList = sq.getGrammars() == null ? List.<GrammarDetailResult>of() : sq.getGrammars().stream()
                    .map(g -> new GrammarDetailResult(
                            g.getId(),
                            g.getReading(),
                            g.getJapanese(),
                            g.getVietnameseMeaningText(),
                            g.getEnglishMeaningText()))
                    .toList();

            speakingQuestionResult = new SpeakingQuestionDetailResult(
                    sq.getId(),
                    sq.getUserId(),
                    sq.getTitle(),
                    sq.getTitleMarkup(),
                    sq.getDescription(),
                    sq.getDescriptionMarkup(),
                    sq.getSampleAnswer(),
                    sq.getStatus(),
                    vocabList,
                    grammarList
            );
        } else if (nodeType.equals(NodeType.VOCABULARY_QUESTION)
                && node.getVocabularyQuestionId() != null) {
            var vq = vocabularyQuestionRepositoryPort
                    .findById(node.getVocabularyQuestionId())
                    .orElseThrow(() -> new ApplicationException(
                            VocabularyQuestionErrorCode.VOCABULARY_QUESTION_NOT_FOUND,
                            VocabularyQuestionDetailMessageKey.VOCABULARY_QUESTION_NOT_FOUND,
                            node.getVocabularyQuestionId()));
            var vocabList = vq.getVocabularies().stream()
                    .map(v -> new VocabularyDetailResult(
                            v.getId(),
                            v.getReading(),
                            v.getJapanese(),
                            v.getVietnameseMeaningText(),
                            v.getEnglishMeaningText()))
                    .toList();
            vocabularyQuestionResult = new VocabularyQuestionDetailResult(
                    vq.getId(),
                    vocabList
            );
        } else if (nodeType.equals(NodeType.CHEST)
                && node.getChestId() != null) {
            var chest = chestRepositoryPort.findById(node.getChestId())
                    .orElseThrow(() -> new ApplicationException(
                            ChestErrorCode.CHEST_NOT_FOUND,
                            ChestDetailMessageKey.CHEST_NOT_FOUND,
                            node.getChestId()));

            chestResult = new ChestResult(
                    chest.getId(),
                    chest.getChestType(),
                    chest.getDescription(),
                    chest.getMinPoint(),
                    chest.getMaxPoint()
            );
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
