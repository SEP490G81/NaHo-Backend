package org.naho.point.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.point.type.PointTransactionType;
import org.naho.question.entity.QuestionEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.topic.entity.LessonEntity;
import org.naho.topic.entity.ObjectiveEntity;
import org.naho.topic.entity.TopicEntity;
import org.naho.user.entity.UserEntity;

import java.time.Instant;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "point_histories")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PointHistoryEntity extends BaseEntity {
    @Column(nullable = false)
    Double point;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    PointTransactionType transactionType;

    @Column(name = "transaction_time", nullable = false)
    Instant transactionTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @ManyToOne
    @JoinColumn(name = "question_id")
    QuestionEntity question;

    @ManyToOne
    @JoinColumn(name = "objective_id")
    ObjectiveEntity objective;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    LessonEntity lesson;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    TopicEntity topic;
}
