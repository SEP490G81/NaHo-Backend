package org.naho.speech.llm.conversation.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.persona.entity.PersonaEntity;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.shared.persistence.BaseEntity;
import org.naho.speech.llm.type.SpeakingSessionStatus;
import org.naho.user.entity.UserEntity;

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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @ManyToOne
    @JoinColumn(name = "persona_id", nullable = false)
    PersonaEntity persona;

    @Column(name = "topic", length = 500)
    String topic;

    @Column(name = "voice_name", length = 100)
    String voiceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "marugoto_level", length = 30)
    MarugotoLevel marugotoLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "formality_level", length = 20)
    FormalityLevel formalityLevel;

    @Column(name = "total_turns", nullable = false)
    Integer totalTurns;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    SpeakingSessionStatus status;

    @Column(name = "started_at", nullable = false)
    Instant startedAt;

    @Column(name = "ended_at")
    Instant endedAt;

    @OneToOne(mappedBy = "speakingSession", cascade = CascadeType.ALL, orphanRemoval = true)
    SpeakingSessionAssessmentEntity speakingSessionAssessment;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    List<SpeakingSessionMessageEntity> speakingSessionMessages;
}
