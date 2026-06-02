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
@Table(name = "question_sample_phrases")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionSamplePhraseEntity extends BaseEntity {
    @Column(name = "vietnamese_meaning_text", columnDefinition = "TEXT")
    String vietnameseMeaningText;

    @Column(name = "english_meaning_text", columnDefinition = "TEXT")
    String englishMeaningText;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    QuestionEntity question;

    @ManyToMany
    @JoinTable(name = "phrase_japanese_tokenizers",
            joinColumns = @JoinColumn(name = "question_sample_phrase_id"),
            inverseJoinColumns = @JoinColumn(name = "japanese_tokenizer_id"))
    List<JapaneseTokenizerEntity> tokenizers;
}
