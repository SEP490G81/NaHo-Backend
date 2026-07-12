package org.naho.question.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.learning.entity.LearningPathNodeEntity;
import org.naho.shared.persistence.BaseEntity;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vocabulary_questions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VocabularyQuestionEntity extends BaseEntity {
    @OneToOne(mappedBy = "vocabularyQuestion")
    LearningPathNodeEntity learningPathNode;
}
