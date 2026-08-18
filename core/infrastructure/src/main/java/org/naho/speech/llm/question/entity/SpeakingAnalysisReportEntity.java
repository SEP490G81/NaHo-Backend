package org.naho.speech.llm.question.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.file.entity.FileEntity;
import org.naho.question.entity.AnswerHistoryEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.speech.azure.entity.SpeechAssessmentEntity;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "speaking_analysis_reports")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeakingAnalysisReportEntity extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "speech_assessment_id", nullable = false)
    SpeechAssessmentEntity speechAssessment;

    @OneToOne
    @JoinColumn(name = "ai_feedback_id", nullable = false)
    AiFeedbackEntity aiFeedback;

    @OneToOne
    @JoinColumn(name = "answer_history_id", nullable = false)
    AnswerHistoryEntity answerHistory;

    @OneToOne
    @JoinColumn(name = "audio_file_id", nullable = false)
    FileEntity audioFile;

    @Column(name = "overall_score", nullable = false)
    Double overallScore;
}
