package org.naho.daily.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.daily.type.MissionStatus;
import org.naho.shared.persistence.BaseEntity;
import org.naho.user.entity.UserEntity;

import java.time.Instant;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_daily_missions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDailyMissionEntity extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    MissionStatus status;

    @Column(name = "completed_at", nullable = false)
    Instant completedAt;

    @Column(name = "earned_at")
    Instant earnedAt;

    @ManyToOne
    @JoinColumn(name = "daily_mission_id", nullable = false)
    DailyMissionEntity dailyMission;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;
}
