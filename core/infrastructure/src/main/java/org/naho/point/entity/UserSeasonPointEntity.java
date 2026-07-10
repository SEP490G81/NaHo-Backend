package org.naho.point.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;
import org.naho.user.entity.UserEntity;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_season_points")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSeasonPointEntity extends BaseEntity {
    @Column(name = "season_point", nullable = false)
    Double seasonPoint;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @ManyToOne
    @JoinColumn(name = "season_id", nullable = false)
    SeasonEntity season;

    @ManyToOne
    @JoinColumn(name = "league_id", nullable = false)
    LeagueEntity league;
}
