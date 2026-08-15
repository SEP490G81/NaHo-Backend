package org.naho.subscription.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;
import org.naho.user.entity.UserEntity;

import java.time.LocalDate;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_daily_ai_usages")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDailyAiUsageEntity extends BaseEntity {
    @Column(name = "usage_date", nullable = false)
    LocalDate usageDate;

    // Số lượt được AI chấm điểm trong Speaking Question
    @Column(name = "speaking_evaluation_count", nullable = false)
    Integer speakingEvaluationCount;

    // Số lượt bắt đầu AI 1:1
    @Column(name = "ai_session_start_count", nullable = false)
    Integer aiSessionStartCount;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;
}
