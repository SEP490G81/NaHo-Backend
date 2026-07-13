package org.naho.question.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.learning.entity.LearningPathNodeEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.vocabulary.entity.VocabularyEntity;

import java.util.ArrayList;
import java.util.List;

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

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "vocabulary_questions_vocabularies",
            joinColumns = @JoinColumn(name = "vocabulary_question_id"),
            inverseJoinColumns = @JoinColumn(name = "vocabulary_id")
    )
    List<VocabularyEntity> vocabularies = new ArrayList<>();
}
