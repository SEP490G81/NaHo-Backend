package org.naho.speech.llm.result;

/**
 * Result DTO: Kết quả từ việc gửi audio message — chứa cả transcript,
 * AI reply, và điểm pronunciation assessment.
 *
 * @param transcribedText    text được nhận dạng từ audio (Speech-to-Text)
 * @param assistantReply     phản hồi từ AI
 * @param accuracyScore      điểm chính xác phát âm (0-100)
 * @param fluencyScore       điểm trôi chảy (0-100)
 * @param completenessScore  điểm hoàn thành (0-100)
 * @param pronunciationScore điểm phát âm tổng thể (0-100)
 */
public record AudioChatResult(
        String transcribedText,
        String assistantReply,
        Double accuracyScore,
        Double fluencyScore,
        Double completenessScore,
        Double pronunciationScore
) {
}
