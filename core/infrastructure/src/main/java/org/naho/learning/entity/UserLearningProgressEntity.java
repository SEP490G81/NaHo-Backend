package org.naho.learning.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;
import org.naho.user.entity.UserEntity;

import java.time.Instant;

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
    @JoinColumn(name = "farthest_available_node_id", nullable = false)
    LearningPathNodeEntity farthestAvailableNode;

    @ManyToOne
    @JoinColumn(name = "last_learning_node_id")
    LearningPathNodeEntity lastLearningNode;

    @Column(name = "last_learning_at")
    Instant lastLearningAt;

    @Column(name = "total_point", nullable = false)
    Double totalPoint;

    @Column(name = "current_streak")
    Integer currentStreak;

    @Column(name = "longest_streak")
    Integer longestStreak;

    @OneToOne(mappedBy = "userLearningProgress")
    UserEntity user;
}
