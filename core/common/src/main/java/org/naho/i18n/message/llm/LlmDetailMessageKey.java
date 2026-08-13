package org.naho.i18n.message.llm;

public final class LlmDetailMessageKey {
    public static final String LLM_API_ERROR = "ai.infrastructure.openai.api.error";
    public static final String LLM_STREAMING_ERROR = "ai.infrastructure.openai.streaming.error";
    public static final String LLM_PARSE_ERROR = "ai.infrastructure.openai.parse.error";
    public static final String LLM_CONNECTION_TIMEOUT = "ai.infrastructure.openai.connection.timeout";

    public static final String LLM_SESSION_NOT_FOUND = "ai.application.session.not.found";
    public static final String LLM_SESSION_ID_INVALID = "ai.application.session.id.invalid";
    public static final String LLM_SESSION_ALREADY_COMPLETED = "ai.application.session.already.completed";
    public static final String LLM_SESSION_TURN_LIMIT_EXCEEDED = "ai.application.session.turn.limit.exceeded";
    public static final String LLM_SESSION_CONCURRENT_LIMIT_EXCEEDED = "ai.application.session.concurrent.limit.exceeded";
    public static final String LLM_DAILY_LIMIT_EXCEEDED = "ai.application.session.daily_limit_exceeded";
    public static final String LLM_SAVE_SESSION_FAILED = "ai.application.session.save_failed";

    private LlmDetailMessageKey() {
    }
}
