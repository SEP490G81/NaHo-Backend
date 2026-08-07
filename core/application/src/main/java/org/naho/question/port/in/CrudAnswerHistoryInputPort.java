package org.naho.question.port.in;

import org.naho.pagination.PageData;
import org.naho.question.command.SpeakingHistoryFilterCommand;
import org.naho.question.result.SpeakingHistoryDetailResult;
import org.naho.question.result.SpeakingHistoryListItemResult;

public interface CrudAnswerHistoryInputPort {
    String generateAudioFilePresignedUrl(Long id, Long userId);

    SpeakingHistoryDetailResult getSpeakingQuestionAnswerHistoryById(Long answerHistoryId);

    PageData<SpeakingHistoryListItemResult> getUserHistoryList(SpeakingHistoryFilterCommand command);
}
