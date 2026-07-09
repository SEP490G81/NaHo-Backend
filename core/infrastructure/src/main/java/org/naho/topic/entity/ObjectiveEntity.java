package org.naho.topic.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.point.entity.PointHistoryEntity;
import org.naho.question.entity.QuestionEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.topic.type.TopicStatus;

import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "objectives")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ObjectiveEntity extends BaseEntity {

    @Column(name = "japanese_name")
    String japaneseName;

    @Column(name = "japanese_description")
    String japaneseDescription;

    @Column(name = "japanese_name_markup", columnDefinition = "TEXT")
    String japaneseNameMarkup;

    @Column(name = "japanese_description_markup", columnDefinition = "TEXT")
    String japaneseDescriptionMarkup;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    TopicStatus status;

    @Column(name = "order_index")
    Double orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    LessonEntity lesson;

    @OneToMany(mappedBy = "objective", cascade = CascadeType.ALL, orphanRemoval = true)
    List<QuestionEntity> questions;

    @OneToMany(mappedBy = "objective", cascade = CascadeType.ALL, orphanRemoval = true)
    List<PointHistoryEntity> pointHistories;
}
