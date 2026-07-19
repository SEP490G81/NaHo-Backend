package org.naho.league.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.file.entity.FileEntity;
import org.naho.shared.persistence.BaseEntity;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "leagues")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeagueEntity extends BaseEntity {
    @Column(nullable = false)
    String name;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(name = "min_point", nullable = false)
    Double minPoint;

    @Column(name = "max_point")
    Double maxPoint;

    @OneToOne
    @JoinColumn(name = "icon_file_id", nullable = false)
    FileEntity iconFile;
}
