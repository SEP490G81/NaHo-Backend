package org.naho.learning.entity;

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
@Table(name = "user_learning_progresses")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLearningProgressEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "learning_path_node_id", nullable = false)
    LearningPathNodeEntity learningPathNode;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;
}
