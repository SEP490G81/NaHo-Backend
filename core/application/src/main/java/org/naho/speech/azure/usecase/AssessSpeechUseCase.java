package org.naho.speech.azure.usecase;

import org.naho.shared.exception.ApplicationException;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.exception.SpeechApplicationErrorCode;
import org.naho.speech.azure.mapper.PronunciationAssessmentMapper;
import org.naho.speech.azure.port.in.AssessSpeechInputPort;
import org.naho.speech.azure.port.out.AzureSpeechService;
import org.naho.speech.azure.result.PronunciationAssessmentResult;
import org.naho.speech.model.PronunciationAssessment;

public class AssessSpeechUseCase implements AssessSpeechInputPort {

    private final AzureSpeechService azureSpeechService;
    private final PronunciationAssessmentMapper pronunciationAssessmentMapper;

    public AssessSpeechUseCase(AzureSpeechService azureSpeechService, PronunciationAssessmentMapper pronunciationAssessmentMapper) {
        this.azureSpeechService = azureSpeechService;
        this.pronunciationAssessmentMapper = pronunciationAssessmentMapper;
    }

    @Override
    public PronunciationAssessmentResult execute(SpeechAssessmentCommand command) {
        if (command.audioBytes() == null || command.audioBytes().length == 0) {
            throw new ApplicationException(
                    SpeechApplicationErrorCode.AUDIO_FILE_INVALID,
                    "Audio content cannot be empty!"
            );
        }

        PronunciationAssessment pronunciationAssessment = azureSpeechService.assess(command);

        return pronunciationAssessmentMapper.modelToResult(pronunciationAssessment);
    }
}