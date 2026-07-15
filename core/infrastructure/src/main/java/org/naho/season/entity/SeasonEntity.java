package org.naho.season.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;

import java.time.Instant;
import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "seasons")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeasonEntity extends BaseEntity {
    @Column(name = "season_no", nullable = false)
    Integer seasonNo;

    @Column(name = "start_at", nullable = false)
    Instant startAt;

    @Column(name = "end_at", nullable = false)
    Instant endAt;

    @OneToMany(mappedBy = "season")
    List<UserSeasonPointEntity> userSeasonPoints;
}
