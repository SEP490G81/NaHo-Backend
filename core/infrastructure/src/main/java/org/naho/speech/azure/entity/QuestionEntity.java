package org.naho.speech.azure.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.file.model.FileEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.speech.type.QuestionStatus;

import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "questions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionEntity extends BaseEntity {
    @Column(name = "question_text", nullable = false)
    String questionText;

    @Column(name = "contextual_hint", columnDefinition = "TEXT")
    String contextualHint;

    @Column(name = "order_index")
    Integer orderIndex;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    QuestionStatus status;

    @OneToOne
    @JoinColumn(name = "question_audio_file_id", nullable = false)
    FileEntity questionAudioFile;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    TopicEntity topic;

    @OneToMany(mappedBy = "question")
    List<QuestionSamplePhraseEntity> questionSamplePhrases;

    @OneToMany(mappedBy = "question")
    List<QuestionVocabularyEntity> questionVocabularies;
}
