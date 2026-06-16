package org.naho.topic.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.category.entity.CategoryEntity;
import org.naho.file.model.FileEntity;
import org.naho.question.entity.QuestionEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.topic.type.TopicStatus;
import org.naho.user.entity.UserEntity;
import org.naho.user.type.JLPTLevel;

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
    String name;

    @Column(name = "japanese_description")
    String description;

    @Column(name = "japanese_name_markup", columnDefinition = "TEXT")
    String japaneseNameMarkup;

    @Column(name = "japanese_description_markup", columnDefinition = "TEXT")
    String japaneseDescriptionMarkup;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    TopicStatus status;

    @Column(name = "jlpt_level", length = 2)
    @Enumerated(EnumType.STRING)
    JLPTLevel jlptLevel;

    @Column(name = "order_index")
    Double orderIndex;

    @OneToOne
    @JoinColumn(name = "cover_image_file_id")
    FileEntity coverImageFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    CategoryEntity category;

    @OneToMany(mappedBy = "topic")
    List<QuestionEntity> questions;
}
