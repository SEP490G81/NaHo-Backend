package org.naho.file.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.naho.shared.persistence.BaseEntity;

import lombok.experimental.SuperBuilder;

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

    @Column(name = "preview_key", length = 2048)
    String previewKey;

    @Column(name = "original_name", nullable = false)
    String originalName;

    @Column(name = "content_type", nullable = false, length = 100)
    String contentType;

    @Column(nullable = false)
    Long size; // bytes
}
