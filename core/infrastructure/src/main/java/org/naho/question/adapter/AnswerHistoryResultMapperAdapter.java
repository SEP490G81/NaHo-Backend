package org.naho.question.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.result.FileResult;
import org.naho.question.command.FindSpeakingQuestionCommand;
import org.naho.question.port.in.GetSpeakingQuestionInputPort;
import org.naho.question.port.out.AnswerHistoryResultMapper;
import org.naho.question.result.AnswerHistoryListItemResult;
import org.naho.question.result.AnswerHistoryResult;
import org.naho.question.result.SpeakingQuestionListItemResult;
import org.naho.question.result.SpeakingQuestionResult;
import org.naho.speech.azure.model.AnswerHistory;
import org.naho.speech.azure.port.in.CrudSpeechAssessmentInputPort;
import org.naho.speech.azure.result.SpeechAssessmentResult;
import org.naho.speech.llm.question.port.in.CrudAiFeedbackInputPort;
import org.naho.speech.llm.question.result.AiFeedbackResult;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnswerHistoryResultMapperAdapter implements AnswerHistoryResultMapper {
    private final GetSpeakingQuestionInputPort getSpeakingQuestionInputPort;
    private final CrudSpeechAssessmentInputPort crudSpeechAssessmentInputPort;
    private final CrudAiFeedbackInputPort crudAiFeedbackInputPort;
    private final CrudFileInputPort crudFileInputPort;

    @Override
    public AnswerHistoryResult domainToResult(AnswerHistory domain) {
        if (domain == null) {
            return null;
        }

        SpeakingQuestionResult speakingQuestionResult = getSpeakingQuestionInputPort
                .findById(new FindSpeakingQuestionCommand(
                        domain.getSpeakingQuestionId(),
                        domain.getUserId()
                ));

        SpeechAssessmentResult speechAssessmentResult = crudSpeechAssessmentInputPort
                .findById(domain.getSpeechAssessmentId());

        AiFeedbackResult aiFeedbackResult = crudAiFeedbackInputPort
                .findById(domain.getAiFeedbackId());

        FileResult fileResult = crudFileInputPort
                .findById(domain.getAudioFileId());

        return AnswerHistoryResult.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .speakingQuestion(speakingQuestionResult)
                .speechAssessment(speechAssessmentResult)
                .aiFeedback(aiFeedbackResult)
                .audioFile(fileResult)
                .duration(domain.getDuration())
                .overallScore(domain.getOverallScore())
                .build();
    }

    @Override
    public AnswerHistoryListItemResult domainToListItemResult(AnswerHistory domain) {
        if (domain == null) {
            return null;
        }

        SpeakingQuestionListItemResult speakingQuestionListItemResult = getSpeakingQuestionInputPort
                .findSpeakingQuestionListItem(new FindSpeakingQuestionCommand(
                        domain.getSpeakingQuestionId(),
                        domain.getUserId()
                ));

        FileResult fileResult = crudFileInputPort
                .findById(domain.getAudioFileId());

        return AnswerHistoryListItemResult.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .speakingQuestion(speakingQuestionListItemResult)
                .audioFile(fileResult)
                .duration(domain.getDuration())
                .overallScore(domain.getOverallScore())
                .build();
    }
}
