package org.naho.speech.llm.conversation.entity;

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
@Table(name = "speaking_improved_expressions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeakingImprovedExpressionEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    SpeakingSessionAssessmentEntity assessment;

    @Column(name = "turn_index")
    Integer turnIndex;

    @Column(name = "original_text", nullable = false, columnDefinition = "TEXT")
    String originalText;

    @Column(name = "improved_text", nullable = false, columnDefinition = "TEXT")
    String improvedText;

    @Column(name = "explanation_vi", columnDefinition = "TEXT")
    String explanationVi;
}
