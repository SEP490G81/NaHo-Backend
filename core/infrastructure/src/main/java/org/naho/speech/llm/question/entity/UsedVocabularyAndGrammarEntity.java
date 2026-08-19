package org.naho.speech.llm.question.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;
import org.naho.speech.llm.type.LanguageCategory;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "used_vocabularies_and_grammars")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsedVocabularyAndGrammarEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "ai_feedback_id", nullable = false)
    AiFeedbackEntity aiFeedback;

    @Column(name = "expression", columnDefinition = "TEXT", nullable = false)
    String expression;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    LanguageCategory category;
}
