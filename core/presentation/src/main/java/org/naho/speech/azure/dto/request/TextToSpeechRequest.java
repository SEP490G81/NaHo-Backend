package org.naho.speech.azure.dto.request;

public record TextToSpeechRequest(
        String text,
        String voiceName, // Tùy chọn (Ví dụ: ja-JP-NanamiNeural, ja-JP-KeitaNeural)
        String language   // Tùy chọn (Ví dụ: ja-JP)
) {
}
