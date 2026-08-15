package org.naho.speech.llm.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.speech.llm.entity.SpeakingSessionMessageEntity;
import org.naho.speech.llm.mapper.SpeakingSessionMessageEntityMapper;
import org.naho.speech.llm.model.SpeakingSessionMessage;
import org.naho.speech.llm.port.out.SpeakingSessionMessageRepositoryPort;
import org.naho.speech.llm.repository.SpeakingSessionMessageJpaRepository;
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
