package org.naho.question.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.file.entity.FileEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.speech.azure.entity.ContentAssessmentEntity;
import org.naho.speech.azure.entity.SpeechAssessmentEntity;
import org.naho.user.entity.UserEntity;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "answer_histories")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerHistoryEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @ManyToOne
    @JoinColumn(name = "speaking_question_id", nullable = false)
    SpeakingQuestionEntity question;

    @OneToOne
    @JoinColumn(name = "audio_file_id", nullable = false)
    FileEntity audioFile;

    @OneToOne(mappedBy = "answerHistory")
    ContentAssessmentEntity contentAssessment;

    @OneToOne(mappedBy = "answerHistory")
    SpeechAssessmentEntity speechAssessment;
}
