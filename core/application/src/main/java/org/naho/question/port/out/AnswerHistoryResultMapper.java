package org.naho.question.port.out;

import org.naho.question.result.AnswerHistoryListItemResult;
import org.naho.question.result.AnswerHistoryResult;
import org.naho.speech.azure.model.AnswerHistory;

public interface AnswerHistoryResultMapper {
    AnswerHistoryResult domainToResult(AnswerHistory domain);

    AnswerHistoryListItemResult domainToListItemResult(AnswerHistory domain);
}
