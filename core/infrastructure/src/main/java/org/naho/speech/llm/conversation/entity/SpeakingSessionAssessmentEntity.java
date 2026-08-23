package org.naho.speech.llm.conversation.entity;

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
@Table(name = "speaking_session_assessments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeakingSessionAssessmentEntity extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "session_id", nullable = false, unique = true)
    SpeakingSessionEntity speakingSession;

    @Column(name = "overall_score", nullable = false)
    Integer overallScore;

    @Column(name = "jlpt_estimate", nullable = false, length = 5)
    String jlptEstimate;

    @Column(name = "fluency_score", nullable = false)
    Integer fluencyScore;

    @Column(name = "pronunciation_score", nullable = false)
    Integer pronunciationScore;

    @Column(name = "grammar_score", nullable = false)
    Integer grammarScore;

    @Column(name = "vocabulary_score", nullable = false)
    Integer vocabularyScore;

    @Column(name = "interaction_score", nullable = false)
    Integer interactionScore;

    @Column(name = "naturalness_score", nullable = false)
    Integer naturalnessScore;

    @Column(name = "coherence_score", nullable = false)
    Integer coherenceScore;

    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    String summary;

    @Column(name = "strengths", nullable = false, columnDefinition = "JSON")
    String strengths;

    @Column(name = "weaknesses", nullable = false, columnDefinition = "JSON")
    String weaknesses;

    @Column(name = "feedback_fluency", columnDefinition = "TEXT")
    String feedbackFluency;

    @Column(name = "feedback_pronunciation", columnDefinition = "TEXT")
    String feedbackPronunciation;

    @Column(name = "feedback_grammar", columnDefinition = "TEXT")
    String feedbackGrammar;

    @Column(name = "feedback_vocabulary", columnDefinition = "TEXT")
    String feedbackVocabulary;

    @Column(name = "feedback_interaction", columnDefinition = "TEXT")
    String feedbackInteraction;

    @Column(name = "feedback_naturalness", columnDefinition = "TEXT")
    String feedbackNaturalness;

    @Column(name = "feedback_coherence", columnDefinition = "TEXT")
    String feedbackCoherence;

    @Column(name = "study_focus_area", length = 30)
    String studyFocusArea;

    @Column(name = "study_recommendation", columnDefinition = "TEXT")
    String studyRecommendation;

    @Column(name = "study_encouragement", columnDefinition = "TEXT")
    String studyEncouragement;

    @Builder.Default
    @OneToMany(mappedBy = "speakingSessionAssessment", cascade = CascadeType.ALL, orphanRemoval = true)
    List<SpeakingImprovedExpressionEntity> speakingImprovedExpressions = new ArrayList<>();
}
