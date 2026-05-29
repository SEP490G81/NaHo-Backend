package org.naho.speech.azure.port.in;

import org.naho.speech.azure.command.TextToSpeechCommand;
import org.naho.speech.azure.result.AudioSpeechResult;

public interface TextToSpeechInputPort {
    AudioSpeechResult execute(TextToSpeechCommand command);
}
