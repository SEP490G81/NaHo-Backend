package org.naho.file.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;
import org.naho.league.entity.LeagueEntity;
import org.naho.persona.entity.PersonaEntity;
import org.naho.question.entity.SpeakingQuestionEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.social.comment.entity.CommentEntity;
import org.naho.social.report.entity.ReportEntity;
import org.naho.user.entity.UserEntity;

import java.time.Instant;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "files")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileEntity extends BaseEntity {
    @Column(name = "object_key", nullable = false, unique = true, length = 500)
    String objectKey;

    @Column(name = "bucket_name")
    String bucketName;

    @Column(name = "original_name", nullable = false)
    String originalFileName;

    @Column(name = "content_type", nullable = false, length = 100)
    String contentType;

    @Column(nullable = false)
    Long size; // bytes

    @Column
    String checksum;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", length = 50)
    OperationType operationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_status", length = 50)
    OperationStatus operationStatus;

    @Column(name = "retry_count", nullable = false)
    Integer retryCount;

    @Column(name = "next_retry_at")
    Instant nextRetryAt;

    @OneToOne(mappedBy = "avatarFile")
    PersonaEntity persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    CommentEntity comment;

    @OneToOne(mappedBy = "speakingQuestionAudioFile")
    SpeakingQuestionEntity speakingQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id") // Tên cột khóa ngoại trong DB của bạn
    ReportEntity report; // Tên biến này BẮT BUỘC phải là "report" để khớp với mappedBy bên kia

    @OneToOne(mappedBy = "iconFile")
    LeagueEntity league;

    @OneToOne(mappedBy = "avatarFile")
    UserEntity user;
}
