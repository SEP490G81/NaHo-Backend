package org.naho.question.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;
import org.naho.vocabulary.entity.VocabularyEntity;

@SuperBuilder
@Entity
@Table(name = "speaking_questions_vocabularies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeakingQuestionVocabularyEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "speaking_question_id", nullable = false)
    SpeakingQuestionEntity speakingQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocabulary_id", nullable = false)
    VocabularyEntity vocabulary;
}
