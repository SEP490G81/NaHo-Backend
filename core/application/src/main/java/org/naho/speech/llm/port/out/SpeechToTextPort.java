package org.naho.speech.llm.port.out;

import org.naho.speech.llm.result.SpeechToTextResult;

/**
 * Output Port: Chuyển đổi audio thành text + đánh giá phát âm.
 * <p>
 * Tầng Application chỉ biết interface này, không biết Azure hay bất kỳ
 * speech provider cụ thể nào (tuân thủ Dependency Rule).
 */
public interface SpeechToTextPort {

    /**
     * Nhận audio bytes, thực hiện speech-to-text và pronunciation assessment.
     *
     * @param audioBytes    dữ liệu audio dạng byte[]
     * @param referenceText text chuẩn để so sánh phát âm (nullable, dùng cho unscripted mode)
     * @return kết quả gồm transcript text và điểm phát âm
     */
    SpeechToTextResult transcribeAndAssess(byte[] audioBytes, String referenceText);
}
