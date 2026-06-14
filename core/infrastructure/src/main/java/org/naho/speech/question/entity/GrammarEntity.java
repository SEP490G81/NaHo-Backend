package org.naho.speech.question.entity;


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
@Table(name = "grammars")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GrammarEntity extends BaseEntity {
    @Column(name = "vietnamese_meaning_text", columnDefinition = "TEXT")
    String vietnameseMeaningText;

    @Column(name = "english_meaning_text", columnDefinition = "TEXT")
    String englishMeaningText;

    @Column(name = "explanation", columnDefinition = "MEDIUMTEXT")
    String explanation;

    @ManyToMany(mappedBy = "grammars")
    List<QuestionEntity> questions;
}
