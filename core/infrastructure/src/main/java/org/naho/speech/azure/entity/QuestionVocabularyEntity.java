package org.naho.speech.azure.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.naho.shared.persistence.BaseEntity;

import java.util.List;

import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question_vocabularies")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionVocabularyEntity extends BaseEntity {
    @Column(name = "vietnamese_meaning_text")
    String vietnameseMeaningText;

    @Column(name = "english_meaning_text")
    String englishMeaningText;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    QuestionEntity question;

    @ManyToMany
    @JoinTable(name = "vocabulary_japanese_tokenizers",
            joinColumns = @JoinColumn(name = "question_vocabulary_id"),
            inverseJoinColumns = @JoinColumn(name = "japanese_tokenizer_id"))
    List<JapaneseTokenizerEntity> tokenizers;
}
