package org.naho.book.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.book.type.CefrLevel;
import org.naho.file.entity.FileEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.user.type.JLPTLevel;

import java.util.ArrayList;
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

    @Column(name = "order_index", nullable = false)
    Double orderIndex;

    @Column(name = "first_node_global_order_index")
    Double firstNodeGlobalOrderIndex;

    @Column(name = "last_node_global_order_index")
    Double lastNodeGlobalOrderIndex;

    @OneToOne
    @JoinColumn(name = "cover_image_file_id")
    FileEntity coverImageFile;

    @Builder.Default
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    List<TopicEntity> topics = new ArrayList<>();
}
