package org.naho.learning.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.book.entity.ObjectiveEntity;
import org.naho.learning.type.NodeType;
import org.naho.point.entity.PointHistoryEntity;
import org.naho.question.entity.ChestEntity;
import org.naho.question.entity.SpeakingQuestionEntity;
import org.naho.question.entity.VocabularyQuestionEntity;
import org.naho.shared.persistence.BaseEntity;

import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "learning_path_nodes")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LearningPathNodeEntity extends BaseEntity {
    @Column(name = "global_order_index", nullable = false, unique = true)
    Double globalOrderIndex;

    @Column(name = "order_index", nullable = false)
    Double orderIndex;

    @Enumerated(EnumType.STRING)
    @Column(name = "node_type", nullable = false)
    NodeType nodeType;

    @ManyToOne
    @JoinColumn(name = "objective_id", nullable = false)
    ObjectiveEntity objective;

    @OneToOne
    @JoinColumn(name = "speaking_question_id")
    SpeakingQuestionEntity speakingQuestion;

    @OneToOne
    @JoinColumn(name = "vocabulary_question_id")
    VocabularyQuestionEntity vocabularyQuestion;

    @OneToOne
    @JoinColumn(name = "chest_id")
    ChestEntity chest;

    @OneToMany(mappedBy = "learningPathNode")
    List<UserNodeProgressEntity> userNodeProgresses;

    @OneToMany(mappedBy = "learningPathNode")
    List<UserLearningProgressEntity> userLearningProgresses;

    @OneToMany(mappedBy = "learningPathNode")
    List<PointHistoryEntity> pointHistories;
}
