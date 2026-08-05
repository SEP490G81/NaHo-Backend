package org.naho.question.port.out;

import org.naho.pagination.PageData;
import org.naho.speech.llm.command.SpeakingHistoryFilterCommand;
import org.naho.speech.llm.result.SpeakingHistoryListItemResult;
import org.naho.speech.model.AnswerHistory;
import org.naho.speech.model.ContentAssessment;
import org.naho.speech.model.SpeechAssessment;
import org.naho.speech.model.WordAssessment;

import java.util.List;
import java.util.Optional;

public interface AnswerHistoryRepositoryPort {
    AnswerHistory saveAnswerHistory(AnswerHistory answerHistory);

    SpeechAssessment saveSpeechAssessment(SpeechAssessment speechAssessment);

    ContentAssessment saveContentAssessment(ContentAssessment contentAssessment);

    List<WordAssessment> saveAllWordAssessment(List<WordAssessment> wordAssessmentList);

    Optional<SpeechAssessment> findSpeechAssessmentByAnswerHistoryId(Long answerHistoryId);

    Optional<ContentAssessment> findContentAssessmentByAnswerHistoryId(Long answerHistoryId);

    PageData<SpeakingHistoryListItemResult> findUserAnswerHistories(SpeakingHistoryFilterCommand command);

    Optional<AnswerHistory> findById(Long id);
}
