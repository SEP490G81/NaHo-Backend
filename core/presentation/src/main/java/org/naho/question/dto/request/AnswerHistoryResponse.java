package org.naho.question.dto.request;

import org.naho.file.result.FileResult;
import org.naho.question.dto.response.SpeakingQuestionResponse;
import org.naho.speech.azure.dto.response.SpeechAssessmentResponse;
import org.naho.speech.llm.question.dto.response.AiFeedbackResponse;

public record AnswerHistoryResponse(
        Long id,
        Long userId,
        SpeakingQuestionResponse speakingQuestion,
        SpeechAssessmentResponse speechAssessment,
        AiFeedbackResponse aiFeedback,
        FileResult audioFile,
        Double duration,
        Double overallScore
) {
}
