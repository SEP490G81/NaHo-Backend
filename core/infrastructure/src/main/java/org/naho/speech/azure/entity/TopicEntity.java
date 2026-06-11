package org.naho.speech.azure.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.file.model.FileEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.speech.type.TopicStatus;
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

    @Column(name = "japanese_name_tokens", columnDefinition = "json")
    String japaneseNameTokens;

    @Column(name = "japanese_description_tokens", columnDefinition = "json")
    String japaneseDescriptionTokens;

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

    @OneToMany(mappedBy = "topic")
    List<QuestionEntity> questions;
}
