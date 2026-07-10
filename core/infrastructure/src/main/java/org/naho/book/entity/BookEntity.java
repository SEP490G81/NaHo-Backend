package org.naho.book.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.book.type.CefrLevel;
import org.naho.file.model.FileEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.user.type.JLPTLevel;

import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookEntity extends BaseEntity {
    @Column(nullable = false)
    String title;

    @Column(columnDefinition = "TEXT")
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "jlpt_level")
    JLPTLevel jlptLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "cefr_level")
    CefrLevel cefrLevel;

    @Column(name = "order_index", nullable = false, unique = true)
    Double orderIndex;

    @OneToOne
    @JoinColumn(name = "cover_image_file_id")
    FileEntity coverImageFile;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    List<TopicEntity> topics;
}
