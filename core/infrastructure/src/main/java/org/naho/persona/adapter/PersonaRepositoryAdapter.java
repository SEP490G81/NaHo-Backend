package org.naho.persona.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.entity.FileEntity;
import org.naho.file.repository.FileJpaRepository;
import org.naho.persona.entity.ConversationStyleEntity;
import org.naho.persona.entity.PersonaEntity;
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.repository.ConversationStyleJpaRepository;
import org.naho.persona.repository.PersonaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PersonaRepositoryAdapter implements PersonaRepositoryPort {

    private final PersonaJpaRepository personaJpaRepository;
    private final ConversationStyleJpaRepository conversationStyleJpaRepository;
    private final FileJpaRepository fileJpaRepository;

    @Override
    public List<Persona> findAll() {
        return personaJpaRepository.findAll().stream()
                .map(this::entityToDomain)
                .toList();
    }

    @Override
    public Optional<Persona> findById(Long id) {
        return personaJpaRepository.findById(id)
                .map(this::entityToDomain);
    }

    @Override
    public Persona save(Persona persona) {
        PersonaEntity entity = domainToEntity(persona);
        PersonaEntity saved = personaJpaRepository.save(entity);
        return entityToDomain(saved);
    }

    private Persona entityToDomain(PersonaEntity entity) {
        return Persona.builder()
                .id(entity.getId())
                .name(entity.getName())
                .prompt(entity.getPrompt())
                .avatarFileId(entity.getAvatarFile() != null ? entity.getAvatarFile().getId() : null)
                .suggestedConversationStyleId(entity.getSuggestedConversationStyle() != null ? entity.getSuggestedConversationStyle().getId() : null)
                .build();
    }

    private PersonaEntity domainToEntity(Persona domain) {
        ConversationStyleEntity style = null;
        if (domain.getSuggestedConversationStyleId() != null) {
            style = conversationStyleJpaRepository.getReferenceById(domain.getSuggestedConversationStyleId());
        }
        FileEntity avatar = null;
        if (domain.getAvatarFileId() != null) {
            avatar = fileJpaRepository.getReferenceById(domain.getAvatarFileId());
        }

        PersonaEntity.PersonaEntityBuilder<?, ?> builder = PersonaEntity.builder()
                .name(domain.getName())
                .prompt(domain.getPrompt())
                .avatarFile(avatar)
                .suggestedConversationStyle(style);

        if (domain.getId() != null) {
            builder.id(domain.getId());
        }

        return builder.build();
    }
}
