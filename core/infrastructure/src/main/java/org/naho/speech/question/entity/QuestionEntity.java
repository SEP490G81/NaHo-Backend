package org.naho.speech.question.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.file.model.FileEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.social.entity.CommentEntity;
import org.naho.social.entity.ReactionEntity;
import org.naho.social.entity.ReportEntity;
import org.naho.speech.topic.entity.TopicEntity;
import org.naho.topic.type.QuestionStatus;
import org.naho.user.entity.UserEntity;

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
    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "title_markup", nullable = false)
    String titleMarkup;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "description_markup", columnDefinition = "TEXT")
    String descriptionMarkup;

    @Column(name = "order_index")
    Double orderIndex;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    QuestionStatus status;

    @OneToOne
    @JoinColumn(name = "question_audio_file_id", nullable = false)
    FileEntity questionAudioFile;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    TopicEntity topic;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @ManyToMany
    @JoinTable(
            name = "questions_grammars",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "grammar_id")
    )
    List<GrammarEntity> grammars;

    @ManyToMany
    @JoinTable(
            name = "questions_vocabularies",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "vocabulary_id")
    )
    List<VocabularyEntity> vocabularies;

    @OneToMany(mappedBy = "question")
    List<CommentEntity> comments;
    
    @OneToMany(mappedBy = "question")
    List<ReportEntity> reports;

    @OneToMany(mappedBy = "question")
    List<ReactionEntity> reactions;
}
