package org.naho.speech.llm.conversation.adapter;

import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.conversation.helper.OpenAiScoringHelper;
import org.naho.speech.llm.conversation.port.out.AiScoringPort;
import org.naho.speech.llm.model.conversation.SpeakingSessionAssessment;
import org.springframework.stereotype.Component;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class OpenAiScoringAdapter implements AiScoringPort {
    private final OpenAiScoringHelper openAiScoringHelper;
    private final HttpClient httpClient;

    public OpenAiScoringAdapter(OpenAiScoringHelper openAiScoringHelper) {
        this.openAiScoringHelper = openAiScoringHelper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    /**
     * Chấm điểm cho 1 session (khi end session)
     *
     * @param sessionCode         code của session cần chấm điểm
     * @param topic               Conversation with + tên của persona
     * @param systemPromptContent system prompt (đã bao gồm cả prompt của persona)
     * @param messagesJson        lịch sử chat của session dưới dạng JSON
     * @return SpeakingSessionAssessmentResult
     */
    @Override
    public SpeakingSessionAssessment score(
            String sessionCode,
            String topic,
            String systemPromptContent,
            String messagesJson
    ) {
        // Build prompt gồm Persona Context, Messages History và Topic Name
        String scoringContext = openAiScoringHelper.buildScoringContext(topic, systemPromptContent, messagesJson);

        // Tạo request body để gửi cho phía OpenAI
        String requestBody = openAiScoringHelper.buildScoringRequestBody(scoringContext);

        HttpRequest httpRequest = openAiScoringHelper.buildHttpRequest(requestBody);

        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() != 200) {
                throw new InfrastructureException(
                        LlmApplicationError.LLM_API_ERROR,
                        LlmDetailMessageKey.LLM_API_ERROR);
            }
            String rawContent = openAiScoringHelper.extractContent(httpResponse.body());
            return openAiScoringHelper.parseScoringResult(rawContent);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InfrastructureException(
                    LlmApplicationError.LLM_CONNECTION_TIMEOUT,
                    LlmDetailMessageKey.LLM_CONNECTION_TIMEOUT,
                    e.getMessage()
            );
        } catch (InfrastructureException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_API_ERROR,
                    LlmDetailMessageKey.LLM_API_ERROR,
                    e.getMessage());
        }
    }
}
