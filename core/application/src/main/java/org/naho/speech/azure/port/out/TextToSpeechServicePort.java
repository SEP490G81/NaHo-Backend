package org.naho.speech.azure.port.out;

import org.naho.speech.azure.result.AudioSpeechResult;

public interface TextToSpeechServicePort {
    AudioSpeechResult textToSpeech(String text, String voiceName, String language);
}
