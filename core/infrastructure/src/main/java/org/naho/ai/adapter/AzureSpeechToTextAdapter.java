package org.naho.ai.adapter;

import org.naho.ai.port.out.SpeechToTextPort;
import org.naho.ai.result.SpeechToTextResult;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.port.out.AzureSpeechService;
import org.naho.speech.model.SpeechAssessment;

/**
 * Bridge Adapter: Nối AI module với Speech module thông qua SpeechToTextPort.
 *
 * Adapter này nằm ở tầng Infrastructure — nơi duy nhất được phép biết
 * cả AiChatPort (AI module) và AzureSpeechService (Speech module).
 *
 * Application layer (UseCase) chỉ biết SpeechToTextPort interface,
 * không biết Azure hay bất kỳ speech provider cụ thể nào.
 */
public class AzureSpeechToTextAdapter implements SpeechToTextPort {

    private final AzureSpeechService azureSpeechService;

    public AzureSpeechToTextAdapter(AzureSpeechService azureSpeechService) {
        this.azureSpeechService = azureSpeechService;
    }

    @Override
    public SpeechToTextResult transcribeAndAssess(byte[] audioBytes, String referenceText) {
        // Gọi Azure Speech Service để nhận dạng giọng nói + đánh giá phát âm
        SpeechAssessmentCommand command = new SpeechAssessmentCommand(audioBytes, referenceText);
        SpeechAssessment assessment = azureSpeechService.assess(command);

        // Map domain model → application result DTO
        return new SpeechToTextResult(
                assessment.getTranscriptText(),
                assessment.getAccuracyScore(),
                assessment.getFluencyScore(),
                assessment.getCompletenessScore(),
                assessment.getPronunciationScore()
        );
    }
}
