package org.naho.book.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.book.type.TopicStatus;
import org.naho.file.entity.FileEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.user.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "topics")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopicEntity extends BaseEntity {
    @Column(name = "japanese_name")
    String japaneseName;

    @Column(name = "japanese_description")
    String japaneseDescription;

    @Column(name = "vietnamese_description", columnDefinition = "TEXT")
    String vietnameseDescription;

    @Column(name = "english_description", columnDefinition = "TEXT")
    String englishDescription;

    @Column(name = "japanese_name_markup", columnDefinition = "TEXT")
    String japaneseNameMarkup;

    @Column(name = "japanese_description_markup", columnDefinition = "TEXT")
    String japaneseDescriptionMarkup;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    TopicStatus status;

    @Column(name = "order_index", nullable = false)
    Double orderIndex;

    @Column(name = "first_node_global_order_index")
    Double firstNodeGlobalOrderIndex;

    @Column(name = "last_node_global_order_index")
    Double lastNodeGlobalOrderIndex;

    @OneToOne
    @JoinColumn(name = "cover_image_file_id")
    FileEntity coverImageFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    UserEntity user;

    @Builder.Default
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    List<LessonEntity> lessons = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "book_id")
    BookEntity book;
}
