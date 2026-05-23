package org.naho.speech.usecase;

import org.naho.shared.exception.DomainException;
import org.naho.speech.dto.SpeechAssessmentRequest;
import org.naho.speech.dto.SpeechAssessmentResponse;
import org.naho.speech.exception.SpeechErrorCode;
import org.naho.speech.model.PronunciationAssessmentResult;
import org.naho.speech.port.in.AssessSpeechInputPort;
import org.naho.speech.port.out.SpeechAssessmentService;

public class AssessSpeechUseCase implements AssessSpeechInputPort {

    private final SpeechAssessmentService speechAssessmentService;

    public AssessSpeechUseCase(SpeechAssessmentService speechAssessmentService) {
        this.speechAssessmentService = speechAssessmentService;
    }

    @Override
    public SpeechAssessmentResponse execute(SpeechAssessmentRequest request) {
        if (request.audioBytes() == null || request.audioBytes().length == 0) {
            throw new DomainException(SpeechErrorCode.AUDIO_FILE_INVALID, "Audio content cannot be empty!");
        }

        // Gọi outbound service để gửi tới Azure Speech AI
        PronunciationAssessmentResult result = speechAssessmentService.assess(
                request.audioBytes(),
                request.referenceText()
        );

        return SpeechAssessmentResponse.fromDomain(result);
    }
}