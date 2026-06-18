package org.naho.question.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;

import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vocabularies")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VocabularyEntity extends BaseEntity {
    @Column(name = "vietnamese_meaning_text")
    String vietnameseMeaningText;

    @Column(name = "english_meaning_text")
    String englishMeaningText;

    @ManyToMany(mappedBy = "vocabularies")
    List<QuestionEntity> questions;
}
