package org.naho.speech.azure.usecase;

import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.exception.AzureSpeechErrorCode;
import org.naho.speech.azure.mapper.PronunciationAssessmentMapper;
import org.naho.speech.azure.port.in.AssessSpeechInputPort;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.azure.result.SpeechAssessmentResult;
import org.naho.speech.model.SpeechAssessment;

public class AssessSpeechUseCase implements AssessSpeechInputPort {

    private final AzureSpeechServicePort azureSpeechServicePort;
    private final PronunciationAssessmentMapper pronunciationAssessmentMapper;

    public AssessSpeechUseCase(AzureSpeechServicePort azureSpeechServicePort, PronunciationAssessmentMapper pronunciationAssessmentMapper) {
        this.azureSpeechServicePort = azureSpeechServicePort;
        this.pronunciationAssessmentMapper = pronunciationAssessmentMapper;
    }

    @Override
    public SpeechAssessmentResult execute(SpeechAssessmentCommand command) {
        if (command.audioBytes() == null || command.audioBytes().length == 0) {
            throw new ApplicationException(
                    AzureSpeechErrorCode.SPEECH_AUDIO_NOT_VALID,
                    SpeechDetailMessageKey.SPEECH_AUDIO_FILE_EMPTY
            );
        }

        SpeechAssessment speechAssessment = azureSpeechServicePort.assess(command);

        return pronunciationAssessmentMapper.modelToResult(speechAssessment);
    }
}