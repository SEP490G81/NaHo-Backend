package org.naho.subscription.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.naho.subscription.type.PlanCode;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanResponse {
    private Long id;
    private PlanCode code;
    private String name;
    private String description;
    private String tier;
    private BigDecimal priceAmount;
    private String priceCurrency;
    private Integer durationDays;
    private int monthlyAssessmentLimit;
    private long monthlyAssessmentAudioSeconds;
    private int maxAssessmentAudioSeconds;
    private long monthlyConversationSeconds;
    private int maxConversationSessionSeconds;
    private int maxConversationTurnsPerSession;
    private Boolean fullCurriculumAccess;
    private Boolean progressAnalyticsEnabled;
    private Boolean sampleAnswerEnabled;
    private Double maxAnswerTimeSeconds;
    private Boolean saveAnswerHistoryEnabled;
    private String status;
}
