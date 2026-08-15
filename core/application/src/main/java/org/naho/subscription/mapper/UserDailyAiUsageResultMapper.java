package org.naho.subscription.mapper;

import org.naho.subscription.model.UserDailyAiUsage;
import org.naho.subscription.result.UserDailyAiUsageResult;

public class UserDailyAiUsageResultMapper {
    public UserDailyAiUsageResult domainToResult(UserDailyAiUsage domain) {
        if (domain == null) {
            return null;
        }

        return UserDailyAiUsageResult.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .usageDate(domain.getUsageDate())
                .speakingEvaluationCount(domain.getSpeakingEvaluationCount())
                .aiSessionStartCount(domain.getAiSessionStartCount())
                .build();
    }
}
