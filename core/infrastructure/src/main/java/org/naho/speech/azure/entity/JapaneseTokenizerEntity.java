package org.naho.speech.azure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.naho.shared.persistence.BaseEntity;

import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "japanese_tokenizers")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JapaneseTokenizerEntity extends BaseEntity {
    @Column(name = "japanese_text", nullable = false)
    String japaneseText;

    @Column(name = "reading_text")
    String readingText;

    @ManyToMany(mappedBy = "tokenizers")
    Set<QuestionVocabularyEntity> questionVocabularies;

    @ManyToMany(mappedBy = "tokenizers")
    Set<QuestionSamplePhraseEntity> questionSamplePhrases;
}
