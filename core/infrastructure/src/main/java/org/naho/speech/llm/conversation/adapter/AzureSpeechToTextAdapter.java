package org.naho.speech.llm.conversation.adapter;

import org.naho.speech.azure.model.SpeechAssessment;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.llm.conversation.port.out.SpeechToTextPort;
import org.naho.speech.llm.conversation.result.SpeechToTextResult;

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
    public SpeechToTextResult transcribeAndAssess(byte[] audioBytes, String referenceText, Long userId) {
        // Gọi Azure Speech Service để nhận dạng giọng nói + đánh giá phát âm
//        SpeechAssessmentCommand command = new SpeechAssessmentCommand(audioBytes, referenceText, userId);
        SpeechAssessment assessment = azureSpeechServicePort.assessAudio(null);

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
