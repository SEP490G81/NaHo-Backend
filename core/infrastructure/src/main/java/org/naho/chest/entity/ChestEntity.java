package org.naho.chest.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.chest.type.ChestType;
import org.naho.daily.entity.DailyMissionEntity;
import org.naho.daily.entity.DailyRewardEntity;
import org.naho.learning.entity.LearningPathNodeEntity;
import org.naho.shared.persistence.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChestEntity extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "chest_type", nullable = false)
    ChestType chestType;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(name = "min_point", nullable = false)
    Integer minPoint;

    @Column(name = "max_point", nullable = false)
    Integer maxPoint;

    @Builder.Default
    @OneToMany(mappedBy = "chest")
    List<LearningPathNodeEntity> learningPathNodes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "chest")
    List<DailyRewardEntity> dailyRewards = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "chest")
    List<DailyMissionEntity> dailyMissions = new ArrayList<>();
}
