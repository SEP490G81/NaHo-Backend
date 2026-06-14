package org.naho.social.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;
import org.naho.social.type.ReactionType;
import org.naho.speech.question.entity.QuestionEntity;
import org.naho.user.entity.UserEntity;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reactions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReactionEntity extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false)
    ReactionType reactionType;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    CommentEntity comment;

    @ManyToOne
    @JoinColumn(name = "question_id")
    QuestionEntity question;
}
