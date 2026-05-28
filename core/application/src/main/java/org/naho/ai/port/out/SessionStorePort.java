package org.naho.ai.port.out;

import java.util.List;
import java.util.Map;

public interface SessionStorePort {
    //Session lifecycle
    void initSession(String sessionId);
    void clearSession(String sessionId);

    //Topic Management
    void setTopic(String sessionId, String topic);
    String getTopic(String sessionId);

    //Conversation History
    void addMessage(String sessionId, String role, String content);
    List<Map<String, String>> getConversationHistory(String sessionId);

    //Transcript
    void appendTranscript(String sessionId, String turn);
    String getFullTranscript(String sessionId);


}
