package org.naho.speech.llm.port.in;

import org.naho.speech.llm.command.SendAudioMessageCommand;
import org.naho.speech.llm.command.SendMessageWithSessionCommand;
import org.naho.speech.llm.command.StartSpeakingCommand;
import org.naho.speech.llm.result.AudioChatResult;
import org.naho.speech.llm.result.ChatResult;
import org.naho.speech.llm.result.SpeakingTopicResult;

import java.util.function.Consumer;

public interface SpeakingSessionInputPort {
    SpeakingTopicResult startTopicSession(StartSpeakingCommand command);

    String startFreeSession(Long personaId);

    ChatResult sendMessage(SendMessageWithSessionCommand command);

    void sendMessageStream(SendMessageWithSessionCommand command, Consumer<String> onToken);

    /**
     * Gửi audio message: Azure STT + Pronunciation Assessment → AI reply.
     */
    AudioChatResult sendAudioMessage(SendAudioMessageCommand command);
}

