package org.naho.speech.mapper;

import org.naho.speech.model.AudioSpeech;
import org.naho.speech.result.AudioSpeechResult;

public class AudioSpeechMapper {
    public AudioSpeechResult modelToResult(AudioSpeech domain) {
        return new AudioSpeechResult(
                domain.getAudioData(),
                domain.getContentType()
        );
    }
}