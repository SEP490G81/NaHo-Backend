package org.naho.speech.llm.question.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ai_feedbacks")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AiFeedbackEntity extends BaseEntity {

    @Column(name = "grammar_score", nullable = false)
    Double grammarScore;

    @Column(name = "vocabulary_score", nullable = false)
    Double vocabularyScore;

    @Column(name = "naturalness_score", nullable = false)
    Double naturalnessScore;

    @Column(name = "content_relevant_score", nullable = false)
    Double contentRelevantScore;

    @Column(name = "average_score", nullable = false)
    Double averageScore;

    @Column(name = "suggest_japanese_answer", columnDefinition = "TEXT", nullable = false)
    String suggestJapaneseAnswer;

    @Column(name = "suggest_answer_translation", columnDefinition = "TEXT", nullable = false)
    String suggestAnswerTranslation;

    @Builder.Default
    @OneToMany(mappedBy = "aiFeedback", cascade = CascadeType.ALL, orphanRemoval = true)
    List<UsedVocabularyAndGrammarEntity> usedVocabulariesAndGrammars = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "aiFeedback", cascade = CascadeType.ALL, orphanRemoval = true)
    List<UserAnswerErrorEntity> userAnswerErrors = new ArrayList<>();
}
