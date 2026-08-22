package org.naho.question.dto.response;

import org.naho.file.result.FileResult;
import org.naho.speech.azure.dto.response.SpeechAssessmentResponse;
import org.naho.speech.llm.question.dto.response.AiFeedbackResponse;

import java.time.Instant;

public record AnswerHistoryResponse(
        Long id,
        Long userId,
        SpeakingQuestionResponse speakingQuestion,
        SpeechAssessmentResponse speechAssessment,
        AiFeedbackResponse aiFeedback,
        FileResult audioFile,
        Double duration,
        Double overallScore,
        Instant createdTime,
        Instant modifiedTime
) {
}
