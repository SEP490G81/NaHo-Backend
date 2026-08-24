package org.naho.speech.llm.conversation.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.llm.conversation.constant.AiMessageField;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.conversation.helper.OpenAiChatHelper;
import org.naho.speech.llm.conversation.port.out.AiChatPort;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAiChatAdapter implements AiChatPort {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final OpenAiChatHelper openAiChatHelper;

    @Override
    public String chatWithContext(List<Map<String, String>> messages) {
        String messagesJson = buildMessagesRequestBody(messages);
        String requestBody = openAiChatHelper.buildContextRequestBody(messagesJson);
        return openAiChatHelper.executeRequest(requestBody, Duration.ofSeconds(60));
    }

    @Override
    public String buildMessagesRequestBody(List<Map<String, String>> messages) {
        try {
            StringBuilder messagesJson = new StringBuilder("[");

            for (Map<String, String> map : messages) {
                messagesJson.append(String.format(
                        "{\"role\":\"%s\",\"content\":%s}",
                        map.get(AiMessageField.ROLE),
                        OBJECT_MAPPER.writeValueAsString(map.get(AiMessageField.CONTENT))
                ));
                messagesJson.append(",");
            }

            // xóa dấu phẩy thừa ở cuối
            if (messagesJson.length() > 1) {
                messagesJson.deleteCharAt(messagesJson.length() - 1);
            }

            messagesJson.append("]");

            return messagesJson.toString();
        } catch (Exception e) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_PARSE_ERROR,
                    LlmDetailMessageKey.LLM_PARSE_ERROR,
                    e.getMessage());
        }
    }
}
