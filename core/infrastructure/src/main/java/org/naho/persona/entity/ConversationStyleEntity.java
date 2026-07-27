package org.naho.persona.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.shared.persistence.BaseEntity;

import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "conversation_styles")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationStyleEntity extends BaseEntity {
    @Column(length = 512)
    String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    String prompt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    FormalityLevel formalityLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "marugoto_level")
    MarugotoLevel marugotoLevel;

    @OneToMany(mappedBy = "suggestedConversationStyle")
    List<PersonaEntity> personas;
}
