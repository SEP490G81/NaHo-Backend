package org.naho.speech.port.out;

import org.naho.speech.model.PronunciationAssessment;

public interface SpeechAssessmentService {
    /**
     * Gửi file audio và tùy chọn referenceText để đánh giá phát âm.
     * Nếu referenceText null hoặc rỗng, sẽ thực hiện đánh giá unscripted (free talk).
     */
    PronunciationAssessment assess(byte[] audioBytes, String referenceText);
}