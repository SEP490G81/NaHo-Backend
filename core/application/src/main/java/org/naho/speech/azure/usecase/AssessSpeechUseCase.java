package org.naho.speech.azure.usecase;

import org.naho.shared.exception.ApplicationException;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.constant.AzureSpeechApplicationMessageKey;
import org.naho.speech.azure.exception.AzureSpeechApplicationErrorCode;
import org.naho.speech.azure.mapper.PronunciationAssessmentMapper;
import org.naho.speech.azure.port.in.AssessSpeechInputPort;
import org.naho.speech.azure.port.out.AzureSpeechService;
import org.naho.speech.azure.result.SpeechAssessmentResult;
import org.naho.speech.model.SpeechAssessment;

public class AssessSpeechUseCase implements AssessSpeechInputPort {

    private final AzureSpeechService azureSpeechService;
    private final PronunciationAssessmentMapper pronunciationAssessmentMapper;

    public AssessSpeechUseCase(AzureSpeechService azureSpeechService, PronunciationAssessmentMapper pronunciationAssessmentMapper) {
        this.azureSpeechService = azureSpeechService;
        this.pronunciationAssessmentMapper = pronunciationAssessmentMapper;
    }

    @Override
    public SpeechAssessmentResult execute(SpeechAssessmentCommand command) {
        if (command.audioBytes() == null || command.audioBytes().length == 0) {
            throw new ApplicationException(
                    AzureSpeechApplicationErrorCode.SPEECH_AUDIO_NOT_VALID,
                    AzureSpeechApplicationMessageKey.SPEECH_AUDIO_FILE_EMPTY
            );
        }

        SpeechAssessment speechAssessment = azureSpeechService.assess(command);

        return pronunciationAssessmentMapper.modelToResult(speechAssessment);
    }
}