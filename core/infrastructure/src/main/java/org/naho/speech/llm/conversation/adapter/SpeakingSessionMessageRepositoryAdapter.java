package org.naho.speech.llm.conversation.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.speech.llm.conversation.entity.SpeakingSessionMessageEntity;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionMessageEntityMapper;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionMessageRepositoryPort;
import org.naho.speech.llm.conversation.repository.SpeakingSessionMessageJpaRepository;
import org.naho.speech.llm.model.conversation.SpeakingSessionMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SpeakingSessionMessageRepositoryAdapter implements SpeakingSessionMessageRepositoryPort {
    private final SpeakingSessionMessageJpaRepository speakingSessionMessageJpaRepository;
    private final SpeakingSessionMessageEntityMapper speakingSessionMessageEntityMapper;

    @Override
    public List<SpeakingSessionMessage> findAllBySessionId(Long sessionId) {
        if (sessionId == null) {
            return List.of();
        }

        List<SpeakingSessionMessageEntity> entities = speakingSessionMessageJpaRepository
                .findBySessionIdOrderByTurnIndexAscIdAsc(sessionId);

        return entities.stream()
                .map(speakingSessionMessageEntityMapper::entityToDomain)
                .toList();
    }
}
