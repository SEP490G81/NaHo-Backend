package org.naho.speech.llm.conversation.port.out;

import java.util.List;
import java.util.Map;


public interface AiChatPort {
    String chatWithContext(List<Map<String, String>> messages);
}
