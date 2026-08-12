package org.naho.social.comment.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.question.entity.SpeakingQuestionEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.social.reaction.type.ReactionType;
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
    @JoinColumn(name = "comment_id", nullable = false)
    CommentEntity comment;

    @ManyToOne
    @JoinColumn(name = "speaking_question_id")
    SpeakingQuestionEntity question;
}
