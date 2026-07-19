package org.naho.chest.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
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
    @Column(nullable = false)
    String title;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(nullable = false)
    Double point;

    @OneToOne(mappedBy = "chest")
    LearningPathNodeEntity learningPathNode;
}
