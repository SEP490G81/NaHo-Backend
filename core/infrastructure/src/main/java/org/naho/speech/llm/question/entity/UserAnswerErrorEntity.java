package org.naho.speech.llm.question.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_answer_errors")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAnswerErrorEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "ai_feedback_id", nullable = false)
    AiFeedbackEntity aiFeedback;

    @Column(name = "incorrect", columnDefinition = "TEXT", nullable = false)
    String incorrect;

    @Column(name = "correction", columnDefinition = "TEXT", nullable = false)
    String correction;
}
