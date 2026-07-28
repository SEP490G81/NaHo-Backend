package org.naho.daily.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.daily.type.MissionType;
import org.naho.shared.persistence.BaseEntity;

import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "daily_missions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DailyMissionEntity extends BaseEntity {
    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "description")
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "mission_type", nullable = false)
    MissionType missionType;

    @Column(nullable = false)
    Double point;

    @OneToMany(mappedBy = "dailyMission")
    List<UserDailyMissionEntity> userDailyMissions;
}
