package org.naho.file.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.naho.shared.BaseEntity;
import org.naho.user.entity.UserEntity;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "files")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileEntity extends BaseEntity {
    @Column(name = "file_url", nullable = false, length = 2048)
    String fileUrl;

    @Column(name = "preview_url", nullable = false, length = 2048)
    String previewUrl;

    @Column(name = "original_name", nullable = false)
    String originalName;

    @Column(name = "content_type", nullable = false, length = 100)
    String contentType;

    @Column(nullable = false)
    Long size; // bytes

    @OneToOne(mappedBy = "avatar")
    UserEntity user;
}
