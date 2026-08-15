package org.naho.i18n.message.llm;

public final class LlmTitleMessageKey {
    public static final String LLM_TOPIC_INVALID_TITLE = "Topic must not be blank";
    public static final String LLM_SESSION_ID_INVALID_TITLE = "Session Id invalid";
    public static final String LLM_SESSION_CODE_INVALID_TITLE = "Session code invalid";
    public static final String LLM_USER_MESSAGE_INVALID_TITLE = "User message must not be blank";
    public static final String LLM_TRANSCRIPT_BLANK_TITLE = "Transcript must not be blank";
    public static final String LLM_SESSION_ALREADY_COMPLETED_TITLE = "Session already completed and evaluated";
    public static final String LLM_SESSION_NOT_FOUND_TITLE = "Speaking session not found";
    public static final String LLM_SESSION_TURN_LIMIT_EXCEEDED_TITLE = "Session turn limit exceeded";
    public static final String LLM_SESSION_CONCURRENT_LIMIT_EXCEEDED_TITLE = "Concurrent session limit exceeded";
    public static final String LLM_DAILY_LIMIT_EXCEEDED_TITLE = "Daily AI speaking session limit reached";
    public static final String LLM_SESSION_ALREADY_STARTED_TITLE = "Speaking session already started";
    public static final String LLM_SAVE_SESSION_FAILED_TITLE = "Failed to save speaking session history!";

    private LlmTitleMessageKey() {
    }
}
