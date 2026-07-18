package org.naho.subscription.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String tier;
    private BigDecimal priceAmount;
    private String priceCurrency;
    private int durationDays;
    private int monthlyAssessmentLimit;
    private long monthlyAssessmentAudioSeconds;
    private int maxAssessmentAudioSeconds;
    private long monthlyConversationSeconds;
    private int maxConversationSessionSeconds;
    private int maxConversationTurnsPerSession;
    private boolean fullCurriculumAccess;
    private boolean progressAnalyticsEnabled;
    private boolean sampleAnswerEnabled;
    private String status;
    private Instant createdTime;
    private Instant modifiedTime;
}
