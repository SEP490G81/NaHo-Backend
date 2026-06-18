package org.naho.file.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.persona.entity.PersonaEntity;
import org.naho.question.entity.QuestionEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.social.entity.CommentEntity;

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

    @ManyToOne
    @JoinColumn(name = "comment_id")
    CommentEntity comment;

    @ManyToOne
    @JoinColumn(name = "question_id")
    QuestionEntity question;
}
