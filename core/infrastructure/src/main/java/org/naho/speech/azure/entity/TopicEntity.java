package org.naho.speech.azure.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.naho.file.model.FileEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.speech.type.TopicStatus;
import org.naho.user.type.JLPTLevel;

import java.util.List;

import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "topics")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopicEntity extends BaseEntity {
    String name;
    String description;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    TopicStatus status;

    @Column(name = "jlpt_level", length = 2)
    @Enumerated(EnumType.STRING)
    JLPTLevel jlptLevel;

    @Column(name = "order_index")
    Integer orderIndex;

    @OneToOne
    @JoinColumn(name = "cover_image_file_id")
    FileEntity coverImageFile;

    @OneToMany(mappedBy = "topic")
    List<QuestionEntity> questions;
}
