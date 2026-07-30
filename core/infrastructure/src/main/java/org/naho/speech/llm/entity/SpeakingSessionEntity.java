package org.naho.speech.llm.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;

import java.time.Instant;
import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "speaking_sessions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeakingSessionEntity extends BaseEntity {

    @Column(name = "session_code", nullable = false, unique = true, length = 36)
    String sessionCode;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "persona_id")
    Long personaId;

    @Column(name = "topic", length = 500)
    String topic;

    @Column(name = "session_type", nullable = false, length = 30)
    String sessionType;

    @Column(name = "marugoto_level", length = 30)
    String marugotoLevel;

    @Column(name = "formality_level", length = 20)
    String formalityLevel;

    @Column(name = "duration_seconds")
    Integer durationSeconds;

    @Column(name = "total_turns", nullable = false)
    int totalTurns;

    @Column(name = "asr_confidence")
    Double asrConfidence;

    @Column(name = "full_transcript", columnDefinition = "LONGTEXT")
    String fullTranscript;

    @Column(name = "status", nullable = false, length = 20)
    String status;

    @Column(name = "started_at", nullable = false)
    Instant startedAt;

    @Column(name = "ended_at")
    Instant endedAt;

    @OneToOne(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    SpeakingSessionAssessmentEntity assessment;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<SpeakingSessionMessageEntity> messages;
}
