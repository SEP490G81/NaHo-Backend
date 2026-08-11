package org.naho.i18n.message.llm;

public final class LlmDetailMessageKey {
    public static final String LLM_API_ERROR = "ai.infrastructure.openai.api.error";
    public static final String LLM_STREAMING_ERROR = "ai.infrastructure.openai.streaming.error";
    public static final String LLM_PARSE_ERROR = "ai.infrastructure.openai.parse.error";
    public static final String LLM_CONNECTION_TIMEOUT = "ai.infrastructure.openai.connection.timeout";

    public static final String LLM_SESSION_NOT_FOUND = "ai.application.session.not.found";
    public static final String LLM_SESSION_ID_INVALID = "ai.application.session.id.invalid";
    public static final String LLM_SESSION_ALREADY_COMPLETED = "ai.application.session.already.completed";

    private LlmDetailMessageKey() {
    }
}
