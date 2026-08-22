package org.naho.question.port.in;

import org.naho.question.result.AnswerHistoryListItemResult;
import org.naho.question.result.AnswerHistoryResult;

import java.util.List;

public interface CrudAnswerHistoryInputPort {
    String generateAudioFilePresignedUrl(Long id, Long userId);

    List<AnswerHistoryListItemResult> findAllBySpeakingQuestionIdAndUserId(Long speakingQuestionId, Long userId);

    AnswerHistoryResult findByAnswerHistoryIdAndUserId(Long answerHistoryId, Long userId);
}
