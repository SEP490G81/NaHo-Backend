package org.naho.persona.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.persona.entity.ConversationStyleEntity;
import org.naho.persona.mapper.ConversationStyleEntityMapper;
import org.naho.persona.model.ConversationStyle;
import org.naho.persona.port.out.ConversationStyleRepositoryPort;
import org.naho.persona.repository.ConversationStyleJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConversationStyleRepositoryAdapter implements ConversationStyleRepositoryPort {

    private final ConversationStyleJpaRepository conversationStyleJpaRepository;
    private final ConversationStyleEntityMapper conversationStyleEntityMapper;

    @Override
    public Optional<ConversationStyle> findById(Long id) {
        return conversationStyleJpaRepository.findById(id)
                .map(conversationStyleEntityMapper::entityToDomain);
    }

    @Override
    public ConversationStyle save(ConversationStyle conversationStyle) {
        ConversationStyleEntity entity = conversationStyleEntityMapper.domainToEntity(conversationStyle);
        ConversationStyleEntity saved = conversationStyleJpaRepository.save(entity);
        return conversationStyleEntityMapper.entityToDomain(saved);
    }
}
