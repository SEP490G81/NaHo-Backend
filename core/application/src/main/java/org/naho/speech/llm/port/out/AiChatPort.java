package org.naho.speech.llm.port.out;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Output Port: Giao tiếp với LLM (OpenAI) để chat.
 * <p>
 * Có 2 cặp method:
 * - chat / chatStream: gửi 1 message đơn giản.
 * - chatWithContext / chatStreamWithContext: gửi full messages array
 * (system prompt + conversation history) để LLM hiểu ngữ cảnh cuộc hội thoại.
 */
public interface AiChatPort {

    /**
     * Chat đơn giản - không có context.
     */
    String chat(String message);

    /**
     * Chat streaming đơn giản - không có context.
     */
    void chatStream(String userMessage, Consumer<String> onToken);

    /**
     * Chat có context: gửi toàn bộ messages array (system + history + user).
     * Mỗi Map chứa {"role": "system|user|assistant", "content": "..."}.
     */
    String chatWithContext(List<Map<String, String>> messages);

    /**
     * Chat streaming có context: gửi toàn bộ messages array.
     * Token được stream qua callback onToken.
     */
    void chatStreamWithContext(List<Map<String, String>> messages, Consumer<String> onToken);
}
