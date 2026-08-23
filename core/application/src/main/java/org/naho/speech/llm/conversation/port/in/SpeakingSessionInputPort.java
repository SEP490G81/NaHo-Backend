package org.naho.speech.llm.conversation.port.in;

import org.naho.speech.llm.conversation.command.SendAudioMessageCommand;
import org.naho.speech.llm.conversation.command.SendTextMessageCommand;
import org.naho.speech.llm.conversation.command.StartSpeakingConversationCommand;
import org.naho.speech.llm.conversation.result.ChatResult;

public interface SpeakingSessionInputPort {
    ChatResult sendMessage(SendTextMessageCommand command);

    String startConversation(StartSpeakingConversationCommand command);

    ChatResult sendAudioMessage(SendAudioMessageCommand command);
}
