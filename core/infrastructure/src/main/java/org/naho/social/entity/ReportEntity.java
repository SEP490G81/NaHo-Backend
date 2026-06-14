package org.naho.social.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;
import org.naho.social.type.ReportType;
import org.naho.speech.question.entity.QuestionEntity;
import org.naho.user.entity.UserEntity;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reports")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportEntity extends BaseEntity {
    @Column(nullable = false)
    String title;

    @Column(nullable = false, length = 512)
    String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ReportType reportType;

    @Builder.Default
    Boolean isResolved = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @ManyToOne
    @JoinColumn(name = "question_id")
    QuestionEntity question;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    CommentEntity comment;
}
