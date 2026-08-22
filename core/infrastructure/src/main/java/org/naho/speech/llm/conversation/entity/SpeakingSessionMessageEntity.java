package org.naho.speech.llm.conversation.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.file.entity.FileEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.speech.llm.type.MessageType;
import org.naho.speech.llm.type.SenderType;

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
    Integer turnIndex;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender_type", nullable = false, length = 20)
    SenderType senderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", length = 20)
    MessageType messageType;

    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    String content;

    @Column(name = "content_translation", nullable = false, columnDefinition = "LONGTEXT")
    String contentTranslation;

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
