package org.naho.daily.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.chest.entity.ChestEntity;
import org.naho.shared.persistence.BaseEntity;

import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "daily_rewards")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DailyRewardEntity extends BaseEntity {
    @Column(name = "reward_year_month", nullable = false)
    String rewardYearMonth;

    @Column(name = "day_of_month", nullable = false)
    Integer dayOfMonth;

    @ManyToOne
    @JoinColumn(name = "chest_id", nullable = false)
    ChestEntity chest;

    @OneToMany(mappedBy = "dailyReward")
    List<UserDailyAttendanceEntity> userDailyAttendances;
}
