package org.naho.vocabulary.usecase;

import org.naho.question.port.out.VocabulariesQuestionPort;
import org.naho.vocabulary.mapper.VocabularyResultMapper;
import org.naho.vocabulary.port.in.GetVocabulariesOfObjectiveInputPort;
import org.naho.vocabulary.result.VocabulariesOfObjectiveResult;
import org.naho.vocabulary.result.VocabularyResult;

import java.util.List;

public class GetVocabulariesOfObjectiveUseCase implements GetVocabulariesOfObjectiveInputPort {

    private final VocabulariesQuestionPort vocabulariesQuestionPort;
    private final VocabularyResultMapper vocabularyResultMapper;

    public GetVocabulariesOfObjectiveUseCase(
            VocabulariesQuestionPort vocabulariesQuestionPort,
            VocabularyResultMapper vocabularyResultMapper
    ) {
        this.vocabulariesQuestionPort = vocabulariesQuestionPort;
        this.vocabularyResultMapper = vocabularyResultMapper;
    }

    @Override
    public VocabulariesOfObjectiveResult getVocabularyListOfObjective(Long objectiveId) {
        List<VocabularyResult> vocabularies = vocabulariesQuestionPort
                .findVocabularyListOfObjective(objectiveId)
                .stream().map(vocabularyResultMapper::domainToResult)
                .toList();

        return new VocabulariesOfObjectiveResult(objectiveId, vocabularies);
    }
}
