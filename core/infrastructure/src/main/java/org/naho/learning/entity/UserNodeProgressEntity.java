package org.naho.learning.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.learning.type.NodeStatus;
import org.naho.shared.persistence.BaseEntity;
import org.naho.user.entity.UserEntity;

import java.time.Instant;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_node_progresses")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserNodeProgressEntity extends BaseEntity {
    @Column(name = "best_score")
    Double bestScore;

    @Column(name = "current_score")
    Double currentScore;

    @Column(name = "attempt_count")
    Integer attemptCount;

    @Column(name = "last_completed_at")
    Instant lastCompletedAt;

    @Enumerated(EnumType.STRING)
    NodeStatus status;

    @ManyToOne
    @JoinColumn(name = "learning_path_node_id", nullable = false)
    LearningPathNodeEntity learningPathNode;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;
}
