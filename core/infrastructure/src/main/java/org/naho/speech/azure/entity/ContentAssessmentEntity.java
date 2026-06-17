package org.naho.speech.azure.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.question.entity.AnswerHistoryEntity;
import org.naho.shared.persistence.BaseEntity;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "content_assessments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContentAssessmentEntity extends BaseEntity {
    @Column(name = "vocabulary_score", nullable = false)
    Double vocabularyScore;

    @Column(name = "grammar_score", nullable = false)
    Double grammarScore;

    @Column(name = "ai_feedback", columnDefinition = "TEXT", nullable = false)
    String aiFeedback;

    @Column(name = "translation_text", columnDefinition = "TEXT")
    String translationText;

    @OneToOne
    @JoinColumn(name = "answer_history_id", nullable = false)
    AnswerHistoryEntity answerHistory;
}
