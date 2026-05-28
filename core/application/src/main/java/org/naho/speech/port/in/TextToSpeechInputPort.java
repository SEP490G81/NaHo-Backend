package org.naho.speech.port.in;

import org.naho.speech.command.TextToSpeechCommand;
import org.naho.speech.result.AudioSpeechResult;

public interface TextToSpeechInputPort {
    AudioSpeechResult execute(TextToSpeechCommand command);
}