package org.naho.speech.llm.dto.response;

/**
 * HTTP Response DTO: Kết quả gửi audio message — transcript + AI reply + điểm phát âm.
 */
public record AudioChatResponse(
        String transcribedText,
        String assistantReply,
        Double accuracyScore,
        Double fluencyScore,
        Double completenessScore,
        Double pronunciationScore
) {
}
