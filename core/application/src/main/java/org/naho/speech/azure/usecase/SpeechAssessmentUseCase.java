package org.naho.speech.azure.usecase;

import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.exception.AzureSpeechErrorCode;
import org.naho.speech.azure.mapper.SpeechAssessmentResultMapper;
import org.naho.speech.azure.model.SpeechAssessment;
import org.naho.speech.azure.port.in.SpeechAssessmentInputPort;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.azure.result.SpeechAssessmentResult;

public class SpeechAssessmentUseCase implements SpeechAssessmentInputPort {
    private final AzureSpeechServicePort azureSpeechServicePort;
    private final SpeechAssessmentResultMapper speechAssessmentResultMapper;

    public SpeechAssessmentUseCase(
            AzureSpeechServicePort azureSpeechServicePort,
            SpeechAssessmentResultMapper speechAssessmentResultMapper
    ) {
        this.azureSpeechServicePort = azureSpeechServicePort;
        this.speechAssessmentResultMapper = speechAssessmentResultMapper;
    }

    @Override
    public SpeechAssessmentResult assessAudio(SpeechAssessmentCommand command) {
        if (command.audioBytes() == null || command.audioBytes().length == 0) {
            throw new ApplicationException(
                    AzureSpeechErrorCode.SPEECH_AUDIO_NOT_VALID,
                    SpeechDetailMessageKey.SPEECH_AUDIO_FILE_EMPTY
            );
        }

        SpeechAssessment speechAssessment = azureSpeechServicePort.assessAudio(command);

        return speechAssessmentResultMapper.modelToResult(speechAssessment);
    }
}
