package org.naho.vocabulary.usecase;

import org.naho.question.port.out.VocabulariesQuestionPort;
import org.naho.vocabulary.model.Vocabulary;
import org.naho.vocabulary.port.in.GetVocabulariesOfObjectiveInputPort;
import org.naho.vocabulary.result.VocabulariesOfObjectiveResult;

import java.util.List;

public class GetVocabulariesOfObjectiveUseCase implements GetVocabulariesOfObjectiveInputPort {

    private final VocabulariesQuestionPort vocabulariesQuestionPort;

    public GetVocabulariesOfObjectiveUseCase(VocabulariesQuestionPort vocabulariesQuestionPort) {
        this.vocabulariesQuestionPort = vocabulariesQuestionPort;
    }

    @Override
    public VocabulariesOfObjectiveResult getVocabularyListOfObjective(int objectiveId) {
        List<Vocabulary> listVocabulary = vocabulariesQuestionPort.findVocabularyListOfObjective(objectiveId);
        if (listVocabulary.isEmpty()) {
            return null;
        }

        List<VocabulariesOfObjectiveResult.VocabularyDetailResult> detailResults = listVocabulary.stream()
                .map(vocab -> new VocabulariesOfObjectiveResult.VocabularyDetailResult(
                        vocab.getId(),
                        vocab.getReading(),
                        vocab.getJapanese(),
                        vocab.getVietnameseMeaningText(),
                        vocab.getEnglishMeaningText()
                ))
                .toList();

        return new VocabulariesOfObjectiveResult(objectiveId, detailResults);
    }
}
