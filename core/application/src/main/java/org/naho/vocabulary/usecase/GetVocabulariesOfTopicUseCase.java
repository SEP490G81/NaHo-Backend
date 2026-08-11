package org.naho.vocabulary.usecase;

import org.naho.i18n.message.question.VocabularyQuestionDetailMessageKey;
import org.naho.question.port.out.VocabulariesQuestionPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.vocabulary.exception.VocabularyErrorCode;
import org.naho.vocabulary.model.Vocabulary;
import org.naho.vocabulary.port.in.GetVocabulariesOfTopicInputPort;
import org.naho.vocabulary.result.VocabulariesOfTopicResult;
import org.naho.vocabulary.result.VocabularyDetailResult;

import java.util.List;

public class GetVocabulariesOfTopicUseCase implements GetVocabulariesOfTopicInputPort {

    private final VocabulariesQuestionPort vocabulariesQuestionPort;

    public GetVocabulariesOfTopicUseCase(VocabulariesQuestionPort vocabulariesQuestionPort) {
        this.vocabulariesQuestionPort = vocabulariesQuestionPort;
    }

    @Override
    public VocabulariesOfTopicResult getVocabularyListOfTopic(Long topicId) {
        List<Vocabulary> listVocabulary = vocabulariesQuestionPort.findVocabularyListOfTopic(topicId);
        if (listVocabulary.isEmpty()) {
            throw new ApplicationException(
                    VocabularyErrorCode.VOCABULARY_NOT_FOUND,
                    VocabularyQuestionDetailMessageKey.VOCABULARY_TOPIC_NOT_FOUND,
                    topicId
            );
        }

        List<VocabularyDetailResult> detailResults = listVocabulary.stream()
                .map(vocab -> new VocabularyDetailResult(
                        vocab.getId(),
                        vocab.getReading(),
                        vocab.getJapanese(),
                        vocab.getVietnameseMeaningText(),
                        vocab.getEnglishMeaningText()
                ))
                .toList();

        return new VocabulariesOfTopicResult(topicId, detailResults);
    }
}
