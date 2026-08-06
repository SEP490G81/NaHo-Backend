package org.naho.speech.llm.port.in;

import org.naho.pagination.PageData;
import org.naho.speech.llm.command.SendAudioMessageCommand;
import org.naho.speech.llm.command.SendMessageWithSessionCommand;
import org.naho.speech.llm.command.SpeakingSessionFilterCommand;
import org.naho.speech.llm.command.StartSpeakingConversationWithAICommand;
import org.naho.speech.llm.result.*;

import java.util.function.Consumer;

public interface SpeakingSessionInputPort {
    ChatResult sendMessage(SendMessageWithSessionCommand command);

    void sendMessageStream(SendMessageWithSessionCommand command, Consumer<String> onToken);

    StartConversationResult startConversationWithAISession(StartSpeakingConversationWithAICommand startSpeakingConversationWithAICommand);

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

    /**
     * Lấy thông tin phiên nói chuyện đang ở trạng thái IN_PROGRESS của người dùng.
     */
    ActiveSpeakingSessionResult getActiveSession(Long userId, Integer personaId);

    /**
     * Khôi phục phiên nói chuyện dở dang từ CSDL/Memory để người dùng tiếp tục hội thoại.
     */
    StartConversationResult resumeSession(String sessionCode, Long userId);
}
