package org.naho.speech.llm.conversation.port.in;

import org.naho.pagination.PageData;
import org.naho.speech.llm.conversation.command.SendAudioMessageCommand;
import org.naho.speech.llm.conversation.command.SendMessageWithSessionCommand;
import org.naho.speech.llm.conversation.command.SpeakingSessionFilterCommand;
import org.naho.speech.llm.conversation.command.StartSpeakingConversationCommand;
import org.naho.speech.llm.conversation.result.*;

import java.util.List;

public interface SpeakingSessionInputPort {
    ChatResult sendMessage(SendMessageWithSessionCommand command);

//    void sendMessageStream(SendMessageWithSessionCommand command, Consumer<String> onToken);

    String startConversation(StartSpeakingConversationCommand command);

    StartConversationResult initFirstGreeting(String sessionCode, Long userId);

    /**
     * Gửi audio message: Azure STT + Pronunciation Assessment → AI reply.
     */
    AudioChatResult sendAudioMessage(SendAudioMessageCommand command);

    /**
     * Lấy danh sách lịch sử các buổi nói AI 1-1 của người dùng (có lọc & phân trang).
     */
    PageData<SpeakingSessionListItemResult> getUserSessionHistories(SpeakingSessionFilterCommand command);

    /**
     * Lấy chi tiết kết quả và toàn bộ nhận xét AI của một buổi nói AI 1-1 theo sessionCode.
     */
    SpeakingSessionDetailResult getSessionHistoryDetail(String sessionCode, Long userId);

    SpeakingSessionResult getInProgressSessionDetails(String sessionCode, Long userId);

    List<SpeakingSessionResult> findAllInProgressSessionsByUserId(Long userId);
}
