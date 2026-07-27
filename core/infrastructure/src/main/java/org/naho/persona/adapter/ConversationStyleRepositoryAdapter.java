package org.naho.persona.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.persona.entity.ConversationStyleEntity;
import org.naho.persona.model.ConversationStyle;
import org.naho.persona.port.out.ConversationStyleRepositoryPort;
import org.naho.persona.repository.ConversationStyleJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConversationStyleRepositoryAdapter implements ConversationStyleRepositoryPort {

    private final ConversationStyleJpaRepository conversationStyleJpaRepository;

    @Override
    public Optional<ConversationStyle> findById(Long id) {
        return conversationStyleJpaRepository.findById(id)
                .map(this::entityToDomain);
    }

    @Override
    public ConversationStyle save(ConversationStyle conversationStyle) {
        ConversationStyleEntity entity = domainToEntity(conversationStyle);
        ConversationStyleEntity saved = conversationStyleJpaRepository.save(entity);
        return entityToDomain(saved);
    }

    private ConversationStyleEntity domainToEntity(ConversationStyle domain) {
        ConversationStyleEntity.ConversationStyleEntityBuilder<?, ?> builder = ConversationStyleEntity.builder()
                .description(domain.getDescription())
                .prompt(domain.getPrompt())
                .formalityLevel(domain.getFormalityLevel())
                .marugotoLevel(domain.getMarugotoLevel());
        if (domain.getId() != null) {
            builder.id(domain.getId());
        }
        return builder.build();
    }

    private ConversationStyle entityToDomain(ConversationStyleEntity entity) {
        return ConversationStyle.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .prompt(entity.getPrompt())
                .formalityLevel(entity.getFormalityLevel())
                .marugotoLevel(entity.getMarugotoLevel())
                .build();
    }
}
