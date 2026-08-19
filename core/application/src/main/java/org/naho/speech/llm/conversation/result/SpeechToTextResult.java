package org.naho.speech.llm.conversation.result;

/**
 * Result DTO: Kết quả từ SpeechToTextPort — chứa transcript và điểm phát âm.
 *
 * @param transcribedText    đoạn text được nhận dạng từ audio
 * @param accuracyScore      điểm chính xác phát âm (0-100)
 * @param fluencyScore       điểm trôi chảy (0-100)
 * @param completenessScore  điểm hoàn thành (0-100)
 * @param pronunciationScore điểm phát âm tổng thể (0-100)
 */
public record SpeechToTextResult(
        String transcribedText,
        Double accuracyScore,
        Double fluencyScore,
        Double completenessScore,
        Double pronunciationScore
) {
}
