package org.naho.speech.llm.usecase;

import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.shared.constant.SystemZoneId;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.llm.exception.LlmApplicationError;
import org.naho.speech.llm.port.in.EndSessionInputPort;
import org.naho.speech.llm.port.out.AiScoringPort;
import org.naho.speech.llm.port.out.SessionStorePort;
import org.naho.speech.llm.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.result.ScoringResult;
import org.naho.subscription.model.UserDailyAiUsage;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.port.out.UserDailyAiUsageRepositoryPort;
import org.naho.subscription.result.SubscriptionPlanResult;

import java.time.Instant;
import java.time.LocalDate;

public class EndSessionUseCase implements EndSessionInputPort {
    private final SessionStorePort sessionStorePort;
    private final AiScoringPort aiScoringPort;
    private final SpeakingSessionRepositoryPort speakingSessionRepositoryPort;
    private final UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort;
    private final GetActiveSubscriptionInputPort getActiveSubscriptionInputPort;

    public EndSessionUseCase(
            SessionStorePort sessionStorePort,
            AiScoringPort aiScoringPort,
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort,
            GetActiveSubscriptionInputPort getActiveSubscriptionInputPort
    ) {
        this.sessionStorePort = sessionStorePort;
        this.aiScoringPort = aiScoringPort;
        this.speakingSessionRepositoryPort = speakingSessionRepositoryPort;
        this.userDailyAiUsageRepositoryPort = userDailyAiUsageRepositoryPort;
        this.getActiveSubscriptionInputPort = getActiveSubscriptionInputPort;
    }

    @Override
    public ScoringResult endSession(Long requestUserId, String sessionId, String topic, String speechMetaData, String arsConfidence) {

        // cần check lại logic chỗ này
        // Lấy user id của người sở hữu cái session này
        Long userId = sessionStorePort.getUserId(sessionId);
        if (userId == null) {
            userId = requestUserId;
        }

        if (userId == null) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SESSION_NOT_FOUND,
                    LlmDetailMessageKey.LLM_SESSION_NOT_FOUND
            );
        }

        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        // lấy số lượt dùng của người dùng trong hôm nay
        UserDailyAiUsage userDailyAiUsage = userDailyAiUsageRepositoryPort
                .findByUserIdAndUsageDateCreateIfNotExists(userId, today);

        // lấy ra gói đăng kí của người dùng hiện tại
        SubscriptionPlanResult subscriptionPlanResult = getActiveSubscriptionInputPort.getUserActiveSubscriptionPlan(userId);

        // kiểm tra nếu nguời dùng đã dùng hết lượt đánh giá session trong ngày hôm nay rồi thì ném ra lỗi
        if (userDailyAiUsage.getAiSessionEvaluationCount() >= subscriptionPlanResult.dailyAiSessionEvaluationLimit()) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_DAILY_LIMIT_EXCEEDED,
                    LlmDetailMessageKey.LLM_DAILY_LIMIT_EXCEEDED
            );
        }

        // Tăng số lần đánh giá AI 1:1 trong ngày của người dùng và lưu lại
        userDailyAiUsage.increaseAiSessionEvaluationCount();
        userDailyAiUsageRepositoryPort.save(userDailyAiUsage);

        String fullTranscript = sessionStorePort.getFullTranscript(sessionId);
        String personaContext = sessionStorePort.getPersonaContext(sessionId);

        String effectiveTopic = (topic != null && !topic.isBlank()) ? topic : sessionStorePort.getTopic(sessionId);

        System.out.println("    Transcript length: " + fullTranscript.length() + " chars");
        System.out.println("    Persona context: " + (personaContext.isBlank() ? "(none)" : personaContext.substring(0, Math.min(80, personaContext.length()))));

        ScoringResult result = aiScoringPort.score(sessionId, effectiveTopic, fullTranscript, speechMetaData, arsConfidence, personaContext);
        System.out.println("    overallScore: " + result.overallScore() + "/100");
        System.out.println("    jlptEstimate: " + result.jlptEstimate());

        // Persist session result to DB
        try {
            Long personaId = sessionStorePort.getPersonaId(sessionId);
            String marugotoLevel = sessionStorePort.getMarugotoLevel(sessionId);
            String formalityLevel = sessionStorePort.getFormalityLevel(sessionId);
            int totalTurns = sessionStorePort.getTurnCount(sessionId);
            Instant startedAt = sessionStorePort.getStartedAt(sessionId);

            Double asrConfidenceDouble = null;
            if (arsConfidence != null && !arsConfidence.isBlank() && !arsConfidence.equals("N/A")) {
                try {
                    asrConfidenceDouble = Double.parseDouble(arsConfidence.trim());
                } catch (NumberFormatException ignored) {
                    throw new ApplicationException(
                            LlmApplicationError.LLM_PARSE_ERROR,
                            LlmDetailMessageKey.LLM_PARSE_ERROR
                    );
                }
            }

            speakingSessionRepositoryPort.saveSpeakingSession(
                    sessionId,
                    userId,
                    personaId,
                    effectiveTopic,
                    marugotoLevel,
                    formalityLevel,
                    fullTranscript,
                    totalTurns,
                    asrConfidenceDouble,
                    startedAt,
                    result
            );
            System.out.println("[EndSessionUseCase] Successfully saved session " + sessionId + " to DB for userId: " + userId);
        } catch (Exception e) {
            System.err.println("[EndSessionUseCase] Failed to persist session to DB: " + e.getMessage());
            throw new ApplicationException(
                    LlmApplicationError.LLM_SAVE_SESSION_FAILED,
                    LlmDetailMessageKey.LLM_SAVE_SESSION_FAILED
            );
        }

        sessionStorePort.clearSession(sessionId);
        return result;
    }
}

