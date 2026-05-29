package org.naho.speech.llm.adapter;

import org.naho.speech.llm.port.out.SpeechToTextPort;
import org.naho.speech.llm.result.SpeechToTextResult;
import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.model.SpeechAssessment;

/**
 * Bridge Adapter: Nối AI module với Speech module thông qua SpeechToTextPort.
 * <p>
 * Adapter này nằm ở tầng Infrastructure — nơi duy nhất được phép biết
 * cả AiChatPort (AI module) và AzureSpeechService (Speech module).
 * <p>
 * Application layer (UseCase) chỉ biết SpeechToTextPort interface,
 * không biết Azure hay bất kỳ speech provider cụ thể nào.
 */
public class AzureSpeechToTextAdapter implements SpeechToTextPort {

    private final AzureSpeechServicePort azureSpeechServicePort;

    public AzureSpeechToTextAdapter(AzureSpeechServicePort azureSpeechServicePort) {
        this.azureSpeechServicePort = azureSpeechServicePort;
    }

    @Override
    public SpeechToTextResult transcribeAndAssess(byte[] audioBytes, String referenceText) {
        // Gọi Azure Speech Service để nhận dạng giọng nói + đánh giá phát âm
        SpeechAssessmentCommand command = new SpeechAssessmentCommand(audioBytes, referenceText);
        SpeechAssessment assessment = azureSpeechServicePort.assess(command);

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
