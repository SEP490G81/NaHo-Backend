package org.naho.vocabulary.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.question.entity.SpeakingQuestionEntity;
import org.naho.question.entity.VocabularyQuestionEntity;
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

    @Column(name = "reading")
    String reading;

    @Column(name = "japanese")
    String japanese;
    //sửa kana -> cách đọc, kanji -> japanese
    @Column(name = "vietnamese_meaning_text")
    String vietnameseMeaningText;

    @Column(name = "english_meaning_text")
    String englishMeaningText;

    // @Column(name = "part_of_speech")
    // String partOfSpeech;

    @ManyToMany(mappedBy = "vocabularies")
    List<SpeakingQuestionEntity> speakingQuestions;
    @ManyToMany(mappedBy = "vocabularies")
    List<VocabularyQuestionEntity> vocabularyQuestions;

}
