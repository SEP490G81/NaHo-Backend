package org.naho.speech.llm.validator;

import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.shared.constant.SystemZoneId;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.llm.exception.LlmApplicationError;
import org.naho.speech.llm.port.out.SessionStorePort;
import org.naho.speech.llm.port.out.SpeakingSessionRepositoryPort;
import org.naho.subscription.model.UserDailyAiUsage;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.port.out.UserDailyAiUsageRepositoryPort;
import org.naho.subscription.result.SubscriptionPlanResult;

import java.time.LocalDate;

public class SessionValidator {
    private final SpeakingSessionRepositoryPort speakingSessionRepositoryPort;
    private final SessionStorePort sessionStorePort;
    private final GetActiveSubscriptionInputPort getActiveSubscriptionInputPort;
    private final UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort;

    public SessionValidator(
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            SessionStorePort sessionStorePort,
            GetActiveSubscriptionInputPort getActiveSubscriptionInputPort,
            UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort
    ) {
        this.speakingSessionRepositoryPort = speakingSessionRepositoryPort;
        this.sessionStorePort = sessionStorePort;
        this.getActiveSubscriptionInputPort = getActiveSubscriptionInputPort;
        this.userDailyAiUsageRepositoryPort = userDailyAiUsageRepositoryPort;
    }

    /**
     * Method check xem session đã được completed chưa
     * nếu đã complete thì sẽ throw exception (do complete rồi thì không được học nữa)
     *
     * @param sessionCode mã phiên chat
     */
    public void validateSessionNotCompleted(String sessionCode) {
        if (speakingSessionRepositoryPort.isSessionCompleted(sessionCode)) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SESSION_ALREADY_COMPLETED,
                    LlmDetailMessageKey.LLM_SESSION_ALREADY_COMPLETED);
        }
    }

    /**
     * Kiểm tra số lượt nói của người dùng trong session
     * Lấy ra gói đăng kí của người dùng và kiểm tra giới hạn
     *
     * @param sessionCode mã session
     * @param userId      id của user
     */
    public void validateSessionTurnLimit(String sessionCode, Long userId) {
        Long targetUserId = userId != null ? userId : sessionStorePort.getUserId(sessionCode);
        if (targetUserId == null) {
            return;
        }

        SubscriptionPlanResult plan = getActiveSubscriptionInputPort
                .getUserActiveSubscriptionPlan(targetUserId);

        int currentTurnCount = sessionStorePort.getTurnCount(sessionCode);

        if (currentTurnCount >= plan.maxTurnsPerAiSession()) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SESSION_TURN_LIMIT_EXCEEDED,
                    LlmDetailMessageKey.LLM_SESSION_TURN_LIMIT_EXCEEDED
            );
        }
    }

    /**
     * Kiểm tra số lượt tạo session của người dùng trong ngày hôm nay
     * Nếu quá số lượt tạo được phép trong 1 ngày thì ném ra lỗi
     *
     * @param userId id của user
     */
    public void validateSessionStartLimit(Long userId) {
        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        // lấy usage AI của người dùng trong hôm nay
        UserDailyAiUsage userDailyAiUsage = userDailyAiUsageRepositoryPort
                .findByUserIdAndUsageDateCreateIfNotExists(userId, today);

        // lấy ra gói đăng kí của người dùng hiện tại
        SubscriptionPlanResult subscriptionPlanResult = getActiveSubscriptionInputPort
                .getUserActiveSubscriptionPlan(userId);

        // kiểm tra nếu nguời dùng hết lượt tạo session AI trong ngày hôm nay rồi thì ném ra lỗi
        if (userDailyAiUsage.getAiSessionStartCount() >= subscriptionPlanResult.dailyAiSessionStartLimit()) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_DAILY_LIMIT_EXCEEDED,
                    LlmDetailMessageKey.LLM_DAILY_LIMIT_EXCEEDED
            );
        }

        // Tăng số lần AI 1:1 trong ngày của người dùng và lưu lại
        userDailyAiUsage.increaseAiSessionStartCount();
        userDailyAiUsageRepositoryPort.save(userDailyAiUsage);
    }

    /**
     * Kiểm tra số session đang trong trạng thái IN_PROGRESS hiện tại của người dùng
     * Có vượt quá giới hạn session trong trạng thái IN_PROGRESS hay không
     *
     * @param userId user id
     */
    public void validateMaxInProgressSession(Long userId) {
        // lấy ra gói đăng kí của người dùng hiện tại
        SubscriptionPlanResult subscriptionPlanResult = getActiveSubscriptionInputPort
                .getUserActiveSubscriptionPlan(userId);

        int currentUserInProgressSessionCount = speakingSessionRepositoryPort
                .countActiveSessionsByUserId(userId);

        // nếu số session đang trong trạng thái IN_PROGRESS
        // lớn hơn giới hạn số IN_PROGRESS session đang có
        // thì ném ra lỗi
        if (currentUserInProgressSessionCount >= subscriptionPlanResult.maxInProgressSessionCount()) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SESSION_CONCURRENT_LIMIT_EXCEEDED,
                    LlmDetailMessageKey.LLM_SESSION_CONCURRENT_LIMIT_EXCEEDED
            );
        }
    }

    /**
     * Kiểm tra xem session có thuộc về user không
     * Nếu không thuộc về user thì ném ra lỗi
     *
     * @param sessionCode mã phiên
     * @param userId      id người dùng
     */
    public void validateSessionIsBelongToUser(String sessionCode, Long userId) {
        if (!speakingSessionRepositoryPort.isSessionBelongToUser(sessionCode, userId)) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SESSION_NOT_FOUND,
                    LlmDetailMessageKey.LLM_SESSION_NOT_FOUND
            );
        }
    }
}
