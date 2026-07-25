package org.naho.usage.model;

import org.naho.i18n.message.usage.UsageDetailMessageKey;
import org.naho.shared.exception.DomainException;
import org.naho.subscription.model.UsageQuota;
import org.naho.usage.exception.UsageDomainErrorCode;

import java.time.Instant;
import java.util.Objects;

public class SubscriptionUsage {
    private final Long id;
    private final Long subscriptionId;
    private final Long userId;
    private final Instant periodStart;
    private final Instant periodEnd;
    private int assessmentAttemptsUsed; //Lượt chấm nói đã dùng
    private long assessmentAudioSecondsUsed; //Tổng giây âm thanh chấm nói đã dùng
    private long conversationSecondsUsed; //Tổng giây hội thoại AI đã dùng
    private int conversationSessionsUsed; //Số phiên hội thoại đã dùng
    private int conversationTurnsUsed; //Số lượt thoại đã dùng
    private long version; //Dùng làm Optimistic Locking khi cập nhật song song

    private SubscriptionUsage(Builder builder) {
        this.id = builder.id;
        this.subscriptionId = builder.subscriptionId;
        this.userId = builder.userId;
        this.periodStart = builder.periodStart;
        this.periodEnd = builder.periodEnd;
        this.assessmentAttemptsUsed = builder.assessmentAttemptsUsed;
        this.assessmentAudioSecondsUsed = builder.assessmentAudioSecondsUsed;
        this.conversationSecondsUsed = builder.conversationSecondsUsed;
        this.conversationSessionsUsed = builder.conversationSessionsUsed;
        this.conversationTurnsUsed = builder.conversationTurnsUsed;
        this.version = builder.version;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SubscriptionUsage create(
            Long subscriptionId,
            Long userId,
            Instant periodStart,
            Instant periodEnd
    ) {
        return builder()
                .subscriptionId(subscriptionId)
                .userId(userId)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .assessmentAttemptsUsed(0)
                .assessmentAudioSecondsUsed(0)
                .conversationSecondsUsed(0)
                .conversationSessionsUsed(0)
                .conversationTurnsUsed(0)
                .version(0L)
                .build();
    }

    public void consumeAssessment(UsageQuota quota, long audioSeconds) {
        if (audioSeconds <= 0) {
            throw new IllegalArgumentException("Audio duration must be positive");
        }
        if (audioSeconds > quota.maxAssessmentAudioSeconds()) {
            throw new DomainException(
                    UsageDomainErrorCode.USAGE_AUDIO_DURATION_EXCEEDED,
                    UsageDetailMessageKey.USAGE_AUDIO_DURATION_EXCEEDED
            );
        }
        if (assessmentAttemptsUsed + 1 > quota.monthlyAssessmentLimit()) {
            throw new DomainException(
                    UsageDomainErrorCode.USAGE_LIMIT_EXCEEDED,
                    UsageDetailMessageKey.USAGE_LIMIT_EXCEEDED
            );
        }
        if (assessmentAudioSecondsUsed + audioSeconds > quota.monthlyAssessmentAudioSeconds()) {
            throw new DomainException(
                    UsageDomainErrorCode.USAGE_LIMIT_EXCEEDED,
                    UsageDetailMessageKey.USAGE_LIMIT_EXCEEDED
            );
        }

        assessmentAttemptsUsed++;
        assessmentAudioSecondsUsed += audioSeconds;
    }

    public void consumeConversationTurn(UsageQuota quota, long userSpeechSeconds) {
        if (userSpeechSeconds <= 0) {
            throw new IllegalArgumentException("Speech duration must be positive");
        }
        if (conversationSecondsUsed + userSpeechSeconds > quota.monthlyConversationSeconds()) {
            throw new DomainException(
                    UsageDomainErrorCode.USAGE_LIMIT_EXCEEDED,
                    UsageDetailMessageKey.USAGE_LIMIT_EXCEEDED
            );
        }

        conversationSecondsUsed += userSpeechSeconds;
        conversationTurnsUsed++;
    }

    // Getters
    public Long getId() { return id; }
    public Long getSubscriptionId() { return subscriptionId; }
    public Long getUserId() { return userId; }
    public Instant getPeriodStart() { return periodStart; }
    public Instant getPeriodEnd() { return periodEnd; }
    public int getAssessmentAttemptsUsed() { return assessmentAttemptsUsed; }
    public long getAssessmentAudioSecondsUsed() { return assessmentAudioSecondsUsed; }
    public long getConversationSecondsUsed() { return conversationSecondsUsed; }
    public int getConversationSessionsUsed() { return conversationSessionsUsed; }
    public int getConversationTurnsUsed() { return conversationTurnsUsed; }
    public long getVersion() { return version; }

    public static final class Builder {
        private Long id;
        private Long subscriptionId;
        private Long userId;
        private Instant periodStart;
        private Instant periodEnd;
        private int assessmentAttemptsUsed;
        private long assessmentAudioSecondsUsed;
        private long conversationSecondsUsed;
        private int conversationSessionsUsed;
        private int conversationTurnsUsed;
        private long version;

        private Builder() {}

        public Builder id(Long id) { this.id = id; return this; }
        public Builder subscriptionId(Long subscriptionId) { this.subscriptionId = subscriptionId; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder periodStart(Instant periodStart) { this.periodStart = periodStart; return this; }
        public Builder periodEnd(Instant periodEnd) { this.periodEnd = periodEnd; return this; }
        public Builder assessmentAttemptsUsed(int assessmentAttemptsUsed) { this.assessmentAttemptsUsed = assessmentAttemptsUsed; return this; }
        public Builder assessmentAudioSecondsUsed(long assessmentAudioSecondsUsed) { this.assessmentAudioSecondsUsed = assessmentAudioSecondsUsed; return this; }
        public Builder conversationSecondsUsed(long conversationSecondsUsed) { this.conversationSecondsUsed = conversationSecondsUsed; return this; }
        public Builder conversationSessionsUsed(int conversationSessionsUsed) { this.conversationSessionsUsed = conversationSessionsUsed; return this; }
        public Builder conversationTurnsUsed(int conversationTurnsUsed) { this.conversationTurnsUsed = conversationTurnsUsed; return this; }
        public Builder version(long version) { this.version = version; return this; }

        public SubscriptionUsage build() {
            if (subscriptionId == null) {
                throw new DomainException(UsageDomainErrorCode.USAGE_SUBSCRIPTION_ID_EMPTY, UsageDetailMessageKey.USAGE_SUBSCRIPTION_ID_EMPTY);
            }
            if (userId == null) {
                throw new DomainException(UsageDomainErrorCode.USAGE_USER_ID_EMPTY, UsageDetailMessageKey.USAGE_USER_ID_EMPTY);
            }
            if (periodStart == null) {
                throw new DomainException(UsageDomainErrorCode.USAGE_PERIOD_START_EMPTY, UsageDetailMessageKey.USAGE_PERIOD_START_EMPTY);
            }
            if (periodEnd == null || !periodEnd.isAfter(periodStart)) {
                throw new DomainException(UsageDomainErrorCode.USAGE_PERIOD_INVALID, UsageDetailMessageKey.USAGE_PERIOD_INVALID);
            }
            return new SubscriptionUsage(this);
        }
    }
}
