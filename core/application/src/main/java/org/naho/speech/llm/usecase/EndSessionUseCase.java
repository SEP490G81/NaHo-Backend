package org.naho.speech.llm.usecase;

import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.shared.constant.SystemZoneId;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.llm.exception.LlmApplicationError;
import org.naho.speech.llm.port.in.EndSessionInputPort;
import org.naho.speech.llm.port.out.AiScoringPort;
import org.naho.speech.llm.port.out.SessionStorePort;
import org.naho.speech.llm.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.result.ScoringResult;
import org.naho.subscription.exception.SubscriptionErrorCode;
import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.model.UserDailyAiUsage;
import org.naho.subscription.port.in.CrudUserDailyAiUsageInputPort;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.port.out.UserDailyAiUsageRepositoryPort;
import org.naho.subscription.type.PlanStatus;
import org.naho.subscription.type.SubscriptionStatus;

import java.time.Instant;
import java.time.LocalDate;

public class EndSessionUseCase implements EndSessionInputPort {
    private final SessionStorePort sessionStorePort;
    private final AiScoringPort aiScoringPort;
    private final SpeakingSessionRepositoryPort speakingSessionRepositoryPort;
    private final CrudUserDailyAiUsageInputPort crudUserDailyAiUsageInputPort;
    private final SubscriptionPlanRepositoryPort subscriptionPlanRepositoryPort;
    private final UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort;

    public EndSessionUseCase(
            SessionStorePort sessionStorePort,
            AiScoringPort aiScoringPort,
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            CrudUserDailyAiUsageInputPort crudUserDailyAiUsageInputPort,
            SubscriptionPlanRepositoryPort subscriptionPlanRepositoryPort,
            UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort
    ) {
        this.sessionStorePort = sessionStorePort;
        this.aiScoringPort = aiScoringPort;
        this.speakingSessionRepositoryPort = speakingSessionRepositoryPort;
        this.crudUserDailyAiUsageInputPort = crudUserDailyAiUsageInputPort;
        this.subscriptionPlanRepositoryPort = subscriptionPlanRepositoryPort;
        this.userDailyAiUsageRepositoryPort = userDailyAiUsageRepositoryPort;
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

        Instant now = Instant.now();
        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        // lấy số lượt dùng của người dùng trong hôm nay
        UserDailyAiUsage userDailyAiUsage = crudUserDailyAiUsageInputPort
                .findByUserIdAndUsageDate(userId, today);

        if (userDailyAiUsage == null) {
            throw new ApplicationException(
                    SubscriptionErrorCode.USER_DAILY_AI_USAGE_NOT_FOUND,
                    SubscriptionDetailMessageKey.USER_DAILY_AI_USAGE_NOT_FOUND
            );
        }

        // lấy ra gói đăng kí của người dùng hiện tại
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepositoryPort
                .findCurrentSubscriptionPlanByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE, PlanStatus.ACTIVE, now)
                .orElseThrow(() -> new ApplicationException(
                        SubscriptionErrorCode.PLAN_NOT_FOUND,
                        SubscriptionDetailMessageKey.PLAN_NOT_FOUND
                ));

        // kiểm tra nếu nguời dùng đã dùng hết lượt đánh giá session trong ngày hôm nay rồi thì ném ra lỗi
        if (userDailyAiUsage.getAiSessionEvaluationCount() >= subscriptionPlan.getDailyAiSessionEvaluationLimit()) {
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
            if (userId == null) {
                userId = requestUserId;
            }

            if (userId == null) {
                System.err.println("[EndSessionUseCase] WARNING: userId is NULL for session " + sessionId + ". Cannot save session history to DB.");
            } else {
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
            }
        } catch (Exception e) {
            System.err.println("[EndSessionUseCase] Failed to persist session to DB: " + e.getMessage());
            e.printStackTrace();
        }

        sessionStorePort.clearSession(sessionId);
        return result;
    }
}

