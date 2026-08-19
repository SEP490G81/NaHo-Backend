package org.naho.grammar.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.question.entity.SpeakingQuestionEntity;
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
    @Column(name = "reading")
    String reading;

    @Column(name = "japanese")
    String japanese;

    @Column(name = "vietnamese_meaning_text", columnDefinition = "TEXT")
    String vietnameseMeaningText;

    @Column(name = "english_meaning_text", columnDefinition = "TEXT")
    String englishMeaningText;

    @ManyToMany(mappedBy = "grammars")
    List<SpeakingQuestionEntity> speakingQuestions;
}
