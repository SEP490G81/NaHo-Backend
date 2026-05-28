package org.naho.speech.port.out;

import org.naho.speech.model.AudioSpeech;

public interface TextToSpeechService {
    /**
     * Tổng hợp văn bản thành âm thanh bằng Azure Speech AI.
     */
    AudioSpeech textToSpeech(String text, String voiceName, String language);
}