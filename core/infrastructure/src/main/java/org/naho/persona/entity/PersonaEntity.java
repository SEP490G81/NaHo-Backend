package org.naho.persona.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.file.model.FileEntity;
import org.naho.shared.persistence.BaseEntity;

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

    @OneToOne
    @JoinColumn(name = "avatar_file_id")
    FileEntity avatarFile;

    @ManyToOne
    @JoinColumn(name = "suggested_conversation_style_id", nullable = false)
    ConversationStyleEntity suggestedConversationStyle;
}
