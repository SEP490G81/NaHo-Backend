package org.naho.speech.azure.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.naho.shared.persistence.BaseEntity;
import org.naho.speech.type.SpeechAssessmentErrorType;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "word_assessments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WordAssessmentEntity extends BaseEntity {
    @Column(nullable = false)
    String word;

    @Column(name = "accuracy_score", nullable = false)
    Double accuracyScore;

    @Column(name = "error_type", nullable = false)
    @Enumerated(EnumType.STRING)
    SpeechAssessmentErrorType errorType;

    @ManyToOne
    @JoinColumn(name = "speech_assessment_id", nullable = false)
    SpeechAssessmentEntity speechAssessment;
}
