package org.naho.speech.llm.conversation.port.in;

import org.naho.speech.llm.conversation.command.SendAudioMessageCommand;
import org.naho.speech.llm.conversation.command.SendMessageWithSessionCommand;
import org.naho.speech.llm.conversation.command.StartSpeakingConversationCommand;
import org.naho.speech.llm.conversation.result.AudioChatResult;
import org.naho.speech.llm.conversation.result.ChatResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionResult;
import org.naho.speech.llm.conversation.result.StartConversationResult;

public interface SpeakingSessionInputPort {
    ChatResult sendMessage(SendMessageWithSessionCommand command);

    String startConversation(StartSpeakingConversationCommand command);

    StartConversationResult initFirstGreeting(String sessionCode, Long userId);

    /**
     * Gửi audio message: Azure STT + Pronunciation Assessment → AI reply.
     */
    AudioChatResult sendAudioMessage(SendAudioMessageCommand command);

    SpeakingSessionResult getInProgressSessionDetails(String sessionCode, Long userId);
}
