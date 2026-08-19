package org.naho.speech.azure.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;

import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "speech_assessments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeechAssessmentEntity extends BaseEntity {
    @Column(name = "transcript_text", nullable = false, columnDefinition = "TEXT")
    String transcriptText;

    @Column(name = "accuracy_score", nullable = false)
    Double accuracyScore;

    @Column(name = "fluency_score", nullable = false)
    Double fluencyScore;

    @Column(name = "completeness_score", nullable = false)
    Double completenessScore;

    @Column(name = "pronunciation_score", nullable = false)
    Double pronunciationScore;

    @Column(name = "average_score", nullable = false)
    Double averageScore;

    @OneToMany(mappedBy = "speechAssessment", cascade = CascadeType.ALL)
    List<WordAssessmentEntity> words;
}
