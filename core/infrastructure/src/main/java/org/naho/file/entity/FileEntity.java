package org.naho.file.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.league.entity.LeagueEntity;
import org.naho.persona.entity.PersonaEntity;
import org.naho.question.entity.SpeakingQuestionEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.social.entity.CommentEntity;
import org.naho.social.report.entity.ReportEntity;
import org.naho.user.entity.UserEntity;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "files")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileEntity extends BaseEntity {
    @Column(name = "object_key", nullable = false, length = 2048)
    String objectKey;

    @Column(name = "original_name", nullable = false)
    String originalName;

    @Column(name = "content_type", nullable = false, length = 100)
    String contentType;

    @Column(nullable = false)
    Long size; // bytes

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
