package org.naho.vocabulary.port.in;

import org.naho.vocabulary.result.VocabulariesOfTopicResult;

public interface GetVocabulariesOfTopicInputPort {
    VocabulariesOfTopicResult getVocabularyListOfTopic(Long topicId);
}
