package org.naho.speech.azure.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.naho.file.model.FileEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.user.entity.UserEntity;

import lombok.experimental.SuperBuilder;

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
    @JoinColumn(name = "question_id", nullable = false)
    QuestionEntity question;

    @OneToOne
    @JoinColumn(name = "audio_file_id", nullable = false)
    FileEntity audioFile;
}
