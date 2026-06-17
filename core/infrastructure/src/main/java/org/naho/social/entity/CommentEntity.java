package org.naho.social.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;
import org.naho.social.report.entity.ReportEntity;
import org.naho.speech.question.entity.QuestionEntity;
import org.naho.user.entity.UserEntity;

import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentEntity extends BaseEntity {
    @Column(columnDefinition = "TEXT", nullable = false)
    String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    QuestionEntity question;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    CommentEntity parent;

    @OneToMany(mappedBy = "parent")
    List<CommentEntity> children;

    @OneToMany(mappedBy = "comment")
    List<ReactionEntity> reactions;

    @OneToMany(mappedBy = "comment")
    List<ReportEntity> reports;
}
