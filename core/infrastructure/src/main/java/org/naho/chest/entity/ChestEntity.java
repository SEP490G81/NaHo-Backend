package org.naho.chest.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.chest.type.ChestType;
import org.naho.learning.entity.LearningPathNodeEntity;
import org.naho.shared.persistence.BaseEntity;

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
    Double minPoint;

    @Column(name = "max_point", nullable = false)
    Double maxPoint;

    @OneToOne(mappedBy = "chest")
    LearningPathNodeEntity learningPathNode;
}
