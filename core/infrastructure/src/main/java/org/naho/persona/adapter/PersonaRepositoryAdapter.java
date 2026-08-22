package org.naho.persona.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.persona.entity.PersonaEntity;
import org.naho.persona.exception.PersonaErrorCode;
import org.naho.persona.mapper.PersonaEntityMapper;
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.repository.PersonaJpaRepository;
import org.naho.persona.type.PersonaStatus;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PersonaRepositoryAdapter implements PersonaRepositoryPort {

    private final PersonaJpaRepository personaJpaRepository;
    private final PersonaEntityMapper personaEntityMapper;

    @Override
    public List<Persona> findAll() {
        return personaJpaRepository.findAll().stream()
                .map(personaEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public Optional<Persona> findById(Long id) {
        return personaJpaRepository
                .findById(id)
                .map(personaEntityMapper::entityToDomain);
    }

    @Override
    public Persona save(Persona persona) {
        PersonaEntity entity = personaEntityMapper.domainToEntity(persona);
        PersonaEntity saved = personaJpaRepository.save(entity);
        return entityToDomain(saved);
    }

    @Override
    public PersonaStatus updatePersonaStatus(Long id, PersonaStatus status) {
        PersonaEntity entity = personaJpaRepository.findById(id)
                .orElseThrow(() -> new InfrastructureException(
                        PersonaErrorCode.PERSONA_NOT_FOUND,
                        PersonaDetailMessageKey.PERSONA_NOT_FOUND,
                        id
                ));

        entity.setStatus(status);
        personaJpaRepository.save(entity);
        return status;
    }

    private Persona entityToDomain(PersonaEntity entity) {
        return Persona.builder()
                .id(entity.getId())
                .name(entity.getName())
                .prompt(entity.getPrompt())
                .avatarFileId(entity.getAvatarFile() != null ? entity.getAvatarFile().getId() : null)
                .suggestedConversationStyleId(entity.getSuggestedConversationStyle() != null ? entity.getSuggestedConversationStyle().getId() : null)
                .status(entity.getStatus())
                .voiceName(entity.getVoiceName())
                .gender(entity.getGender())
                .build();
    }
}
