package org.naho.speech.usecase;

import org.naho.shared.exception.ApplicationException;
import org.naho.speech.command.SpeechAssessmentCommand;
import org.naho.speech.exception.SpeechApplicationErrorCode;
import org.naho.speech.mapper.PronunciationAssessmentMapper;
import org.naho.speech.result.PronunciationAssessmentResult;
import org.naho.speech.model.PronunciationAssessment;
import org.naho.speech.port.in.AssessSpeechInputPort;
import org.naho.speech.port.out.SpeechAssessmentService;

public class AssessSpeechUseCase implements AssessSpeechInputPort {

    private final SpeechAssessmentService speechAssessmentService;
    private final PronunciationAssessmentMapper pronunciationAssessmentMapper;

    public AssessSpeechUseCase(SpeechAssessmentService speechAssessmentService, PronunciationAssessmentMapper pronunciationAssessmentMapper) {
        this.speechAssessmentService = speechAssessmentService;
        this.pronunciationAssessmentMapper = pronunciationAssessmentMapper;
    }

    @Override
    public PronunciationAssessmentResult execute(SpeechAssessmentCommand request) {
        if (request.audioBytes() == null || request.audioBytes().length == 0) {
            throw new ApplicationException(SpeechApplicationErrorCode.AUDIO_FILE_INVALID, "Audio content cannot be empty!");
        }

        // Gọi outbound service để gửi tới Azure Speech AI
        PronunciationAssessment pronunciationAssessment = speechAssessmentService.assess(
                request.audioBytes(),
                request.referenceText()
        );

        return pronunciationAssessmentMapper.modelToResult(pronunciationAssessment);
    }
}