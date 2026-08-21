package org.naho.question.port.in;

import org.naho.question.result.AnswerHistoryListItemResult;

import java.util.List;

public interface CrudAnswerHistoryInputPort {
    String generateAudioFilePresignedUrl(Long id, Long userId);

    List<AnswerHistoryListItemResult> findAllBySpeakingQuestionIdAndUserId(Long speakingQuestionId, Long userId);
}
