package org.naho.speech.azure.command;

public record TextToSpeechCommand(
        String text,
        String voiceName, // Tùy chọn (Ví dụ: ja-JP-NanamiNeural, ja-JP-KeitaNeural)
        String language   // Tùy chọn (Ví dụ: ja-JP)
) {
}
