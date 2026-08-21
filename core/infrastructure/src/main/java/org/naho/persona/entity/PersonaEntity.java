package org.naho.persona.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.file.entity.FileEntity;
import org.naho.persona.type.PersonaStatus;
import org.naho.shared.persistence.BaseEntity;
import org.naho.user.type.Gender;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "personas")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonaEntity extends BaseEntity {
    @Column(unique = true, nullable = false)
    String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    String prompt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    PersonaStatus status;

    @Column(name = "voice_name", length = 100)
    String voiceName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    Gender gender;

    @OneToOne
    @JoinColumn(name = "avatar_file_id")
    FileEntity avatarFile;

    @ManyToOne
    @JoinColumn(name = "suggested_conversation_style_id", nullable = false)
    ConversationStyleEntity suggestedConversationStyle;
}
