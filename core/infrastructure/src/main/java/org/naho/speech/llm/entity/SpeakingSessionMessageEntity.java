package org.naho.speech.llm.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.file.entity.FileEntity;
import org.naho.shared.persistence.BaseEntity;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "speaking_session_messages")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeakingSessionMessageEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    SpeakingSessionEntity session;

    @Column(name = "turn_index", nullable = false)
    int turnIndex;

    @Column(name = "sender_type", nullable = false, length = 20)
    String senderType;

    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    String content;

    @Column(name = "corrected_text", columnDefinition = "TEXT")
    String correctedText;

    @Column(name = "correction_explanation", columnDefinition = "TEXT")
    String correctionExplanation;

    @Column(name = "grammar_note", columnDefinition = "TEXT")
    String grammarNote;

    @Column(name = "hint_for_learner", columnDefinition = "TEXT")
    String hintForLearner;

    @Column(name = "pronunciation_score")
    Double pronunciationScore;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audio_file_id")
    FileEntity audioFile;
}
