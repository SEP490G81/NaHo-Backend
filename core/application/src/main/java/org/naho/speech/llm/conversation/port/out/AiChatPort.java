package org.naho.speech.llm.conversation.port.out;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public interface AiChatPort {

    String chat(String message);

    void chatStream(String userMessage, Consumer<String> onToken);

    //gui lai de AI hieu toan bo ngu canh
    String chatWithContext(List<Map<String, String>> messages);


    void chatStreamWithContext(List<Map<String, String>> messages, Consumer<String> onToken);
}
