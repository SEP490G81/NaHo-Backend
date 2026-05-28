package org.naho.ai.port.in;

import org.naho.ai.command.SendAudioMessageCommand;
import org.naho.ai.command.SendMessageWithSessionCommand;
import org.naho.ai.command.StartSpeakingCommand;
import org.naho.ai.result.AudioChatResult;
import org.naho.ai.result.ChatResult;
import org.naho.ai.result.SpeakingTopicResult;

import java.util.function.Consumer;

public interface SpeakingSessionInputPort {
    SpeakingTopicResult startTopicSession(StartSpeakingCommand command);
    String startFreeSession();
    ChatResult sendMessage(SendMessageWithSessionCommand command);
    void sendMessageStream(SendMessageWithSessionCommand command, Consumer<String> onToken);

    /**
     * Gửi audio message: Azure STT + Pronunciation Assessment → AI reply.
     */
    AudioChatResult sendAudioMessage(SendAudioMessageCommand command);
}

