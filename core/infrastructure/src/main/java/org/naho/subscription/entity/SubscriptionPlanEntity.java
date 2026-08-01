package org.naho.subscription.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;
import org.naho.subscription.type.PlanCode;
import org.naho.subscription.type.PlanStatus;
import org.naho.subscription.type.PlanTier;

import java.math.BigDecimal;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscription_plans")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionPlanEntity extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    PlanCode code;

    @Column(nullable = false)
    String name;

    @Column(columnDefinition = "TEXT")
    String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PlanTier tier;

    @Column(name = "price_amount", nullable = false)
    BigDecimal priceAmount;

    @Column(name = "price_currency", nullable = false)
    String priceCurrency;

    @Column(name = "duration_days", nullable = false)
    Integer durationDays;

    @Column(name = "monthly_assessment_limit", nullable = false)
    Integer monthlyAssessmentLimit;

    @Column(name = "monthly_assessment_audio_seconds", nullable = false)
    Long monthlyAssessmentAudioSeconds;

    @Column(name = "max_assessment_audio_seconds", nullable = false)
    Integer maxAssessmentAudioSeconds;

    @Column(name = "monthly_conversation_seconds", nullable = false)
    Long monthlyConversationSeconds;

    @Column(name = "max_conversation_session_seconds", nullable = false)
    Integer maxConversationSessionSeconds;

    @Column(name = "max_conversation_turns_per_session", nullable = false)
    Integer maxConversationTurnsPerSession;

    @Column(name = "full_curriculum_access", nullable = false)
    Boolean fullCurriculumAccess;

    @Column(name = "progress_analytics_enabled", nullable = false)
    Boolean progressAnalyticsEnabled;

    @Column(name = "sample_answer_enabled", nullable = false)
    Boolean sampleAnswerEnabled;

    @Column(name = "max_answer_time_seconds", nullable = false)
    Double maxAnswerTimeSeconds;

    @Column(name = "save_answer_history_enabled", nullable = false)
    Boolean saveAnswerHistoryEnabled;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PlanStatus status;
}
