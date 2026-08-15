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

    @Column(columnDefinition = "TEXT")
    String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PlanTier tier;

    @Column(name = "price_amount", nullable = false)
    BigDecimal priceAmount;

    @Column(name = "price_currency", nullable = false)
    String priceCurrency;

    @Column(name = "duration_days")
    Integer durationDays;

    @Column(name = "daily_speaking_question_evaluation_limit", nullable = false)
    Integer dailySpeakingQuestionEvaluationLimit;

    @Column(name = "max_speaking_question_recording_seconds", nullable = false)
    Integer maxSpeakingQuestionRecordingSeconds;

    @Column(name = "max_turns_per_ai_session", nullable = false)
    Integer maxTurnsPerAiSession;

    @Column(name = "daily_ai_session_start_limit", nullable = false)
    Integer dailyAiSessionStartLimit;

    @Column(name = "max_ai_turn_speaking_seconds", nullable = false)
    Integer maxAiTurnSpeakingSeconds;

    @Column(name = "sample_answer_enabled", nullable = false)
    Boolean sampleAnswerEnabled;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PlanStatus status;
}
