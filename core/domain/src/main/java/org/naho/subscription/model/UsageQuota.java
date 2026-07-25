package org.naho.subscription.model;

import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.shared.exception.DomainException;
import org.naho.subscription.exception.SubscriptionDomainErrorCode;

public record UsageQuota(
        int monthlyAssessmentLimit,
        long monthlyAssessmentAudioSeconds,
        int maxAssessmentAudioSeconds,
        long monthlyConversationSeconds,
        int maxConversationSessionSeconds,
        int maxConversationTurnsPerSession) {
    public UsageQuota {
        if (monthlyAssessmentLimit < 0) {
            throw new DomainException(SubscriptionDomainErrorCode.PLAN_QUOTA_EMPTY,
                    SubscriptionDetailMessageKey.PLAN_QUOTA_EMPTY);
        }
        if (monthlyAssessmentAudioSeconds < 0 || maxAssessmentAudioSeconds < 0) {
            throw new DomainException(SubscriptionDomainErrorCode.PLAN_QUOTA_EMPTY,
                    SubscriptionDetailMessageKey.PLAN_QUOTA_EMPTY);
        }
        if (monthlyConversationSeconds < 0 || maxConversationSessionSeconds < 0 || maxConversationTurnsPerSession < 0) {
            throw new DomainException(SubscriptionDomainErrorCode.PLAN_QUOTA_EMPTY,
                    SubscriptionDetailMessageKey.PLAN_QUOTA_EMPTY);
        }
    }
}
