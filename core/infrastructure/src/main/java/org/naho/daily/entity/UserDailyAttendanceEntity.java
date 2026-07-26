package org.naho.daily.entity;

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
@Table(name = "user_daily_attendances")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDailyAttendanceEntity extends BaseEntity {
    @Column(name = "attendance_date", nullable = false)
    LocalDate attendanceDate;

    @Column(name = "earned_point", nullable = false)
    Integer earnedPoint;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @ManyToOne
    @JoinColumn(name = "daily_reward_id", nullable = false)
    DailyRewardEntity dailyReward;
}
