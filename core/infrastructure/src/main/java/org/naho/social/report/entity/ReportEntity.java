package org.naho.social.report.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.file.entity.FileEntity;
import org.naho.question.entity.SpeakingQuestionEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.social.entity.CommentEntity;
import org.naho.social.report.type.ReportType;
import org.naho.user.entity.UserEntity;

import java.util.List;

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

    @Column(nullable = false, columnDefinition = "TEXT")
    String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    ReportType reportType;

    @Builder.Default
    Boolean isResolved = false;

    @Column(columnDefinition = "TEXT")
    String adminReply;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @ManyToOne
    @JoinColumn(name = "speaking_question_id")
    SpeakingQuestionEntity question;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    CommentEntity comment;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    List<FileEntity> files;
}
