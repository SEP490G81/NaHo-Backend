package org.naho.speech.azure.usecase;

import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.azure.mapper.SpeechAssessmentResultMapper;
import org.naho.speech.azure.model.SpeechAssessment;
import org.naho.speech.azure.port.in.CrudSpeechAssessmentInputPort;
import org.naho.speech.azure.port.out.SpeechAssessmentRepositoryPort;
import org.naho.speech.azure.result.SpeechAssessmentResult;

public class CrudSpeechAssessmentUseCase implements CrudSpeechAssessmentInputPort {
    private final SpeechAssessmentResultMapper speechAssessmentResultMapper;
    private final SpeechAssessmentRepositoryPort speechAssessmentRepositoryPort;

    public CrudSpeechAssessmentUseCase(
            SpeechAssessmentResultMapper speechAssessmentResultMapper,
            SpeechAssessmentRepositoryPort speechAssessmentRepositoryPort
    ) {
        this.speechAssessmentResultMapper = speechAssessmentResultMapper;
        this.speechAssessmentRepositoryPort = speechAssessmentRepositoryPort;
    }

    @Override
    public SpeechAssessmentResult findById(Long id) {
        if (id == null) {
            throw new ApplicationException(
                    SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                    SpeakingQuestionDetailMessageKey.SPEECH_ASSESSMENT_NOT_FOUND
            );
        }

        SpeechAssessment speechAssessment = speechAssessmentRepositoryPort.findById(id)
                .orElseThrow(() -> new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.SPEECH_ASSESSMENT_NOT_FOUND,
                        id
                ));

        return speechAssessmentResultMapper.modelToResult(speechAssessment);
    }
}

