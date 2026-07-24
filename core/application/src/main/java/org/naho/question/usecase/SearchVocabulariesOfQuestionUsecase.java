package org.naho.question.usecase;

import org.naho.question.command.LearningPathNodeCommand;
import org.naho.vocabulary.model.Vocabulary;
import org.naho.question.port.in.SearchVocabulariesOfQuestionInputPort;
import org.naho.vocabulary.port.out.VocabularyPort;
import org.naho.question.result.VocabulariesOfQuestionResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.vocabulary.exception.VocabularyErrorCode;
import org.naho.i18n.message.question.VocabularyQuestionDetailMessageKey;

import java.util.List;

public class SearchVocabulariesOfQuestionUsecase implements SearchVocabulariesOfQuestionInputPort {

    private final VocabularyPort vocabularyPort;

    public SearchVocabulariesOfQuestionUsecase(VocabularyPort vocabularyPort) {
        this.vocabularyPort = vocabularyPort;
    }


    @Override
    public VocabulariesOfQuestionResult getVocabularyListOfQuestion(LearningPathNodeCommand learningPathNodeCommand) {
        List<Vocabulary> listVocabulary = vocabularyPort.findVocabularyList(learningPathNodeCommand.vocabulary_question_id());
        if (listVocabulary.isEmpty()) {
            throw new ApplicationException(
                    VocabularyErrorCode.VOCABULARY_NOT_FOUND,
                    VocabularyQuestionDetailMessageKey.VOCABULARY_NOT_FOUND,
                    learningPathNodeCommand.vocabulary_question_id()
            );
        }
        List<VocabulariesOfQuestionResult.VocabularyDetailResult> vocabularyDetailResultList = listVocabulary.stream().map(
                vocab -> new VocabulariesOfQuestionResult.VocabularyDetailResult(
                        vocab.getId(),
                        vocab.getReading(),
                        vocab.getJapanese(),
                        vocab.getVietnameseMeaningText(),
                        vocab.getEnglishMeaningText()
                )
        ).toList();
        VocabulariesOfQuestionResult result = new VocabulariesOfQuestionResult(
                learningPathNodeCommand.id(),
                learningPathNodeCommand.vocabulary_question_id(),
                vocabularyDetailResultList
        );
        return result;
    }
}
