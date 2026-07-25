package org.naho.speech.llm.port.in;

import org.naho.speech.llm.command.SendAudioMessageCommand;
import org.naho.speech.llm.command.SendMessageWithSessionCommand;
import org.naho.speech.llm.command.StartSpeakingConversationWithAICommand;
import org.naho.speech.llm.command.StartSpeakingTopicCommand;
import org.naho.speech.llm.result.AudioChatResult;
import org.naho.speech.llm.result.ChatResult;
import org.naho.speech.llm.result.SpeakingTopicResult;
import org.naho.speech.llm.result.StartConversationResult;

import java.util.function.Consumer;

public interface SpeakingSessionInputPort {
    SpeakingTopicResult startTopicSession(StartSpeakingTopicCommand command);

    ChatResult sendMessage(SendMessageWithSessionCommand command);

    void sendMessageStream(SendMessageWithSessionCommand command, Consumer<String> onToken);

    StartConversationResult startConversationWithAISession(StartSpeakingConversationWithAICommand startSpeakingConversationWithAICommand);

    /**
     * Gửi audio message: Azure STT + Pronunciation Assessment → AI reply.
     */
    AudioChatResult sendAudioMessage(SendAudioMessageCommand command);
}

