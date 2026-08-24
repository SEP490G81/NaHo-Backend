package org.naho.speech.llm.conversation.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.speech.llm.conversation.entity.SpeakingSessionAssessmentEntity;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionAssessmentEntityMapper;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionAssessmentRepositoryPort;
import org.naho.speech.llm.conversation.repository.SpeakingSessionAssessmentJpaRepository;
import org.naho.speech.llm.model.conversation.SpeakingSessionAssessment;
import org.naho.speech.llm.type.SpeakingSessionStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SpeakingSessionAssessmentRepositoryAdapter implements SpeakingSessionAssessmentRepositoryPort {
    private final SpeakingSessionAssessmentJpaRepository speakingSessionAssessmentJpaRepository;
    private final SpeakingSessionAssessmentEntityMapper speakingSessionAssessmentEntityMapper;

    @Override
    public SpeakingSessionAssessment save(SpeakingSessionAssessment speakingSessionAssessment) {
        SpeakingSessionAssessmentEntity entity = speakingSessionAssessmentEntityMapper.domainToEntity(speakingSessionAssessment);
        SpeakingSessionAssessmentEntity savedEntity = speakingSessionAssessmentJpaRepository.save(entity);
        return speakingSessionAssessmentEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public SpeakingSessionAssessment findById(Long id) {
        return null;
    }

    @Override
    public Optional<SpeakingSessionAssessment> findBySpeakingSession_SessionCodeAndSpeakingSession_StatusAndSpeakingSession_User_Id(
            String speakingSessionSessionCode,
            SpeakingSessionStatus speakingSessionStatus,
            Long speakingSessionUserId
    ) {
        return speakingSessionAssessmentJpaRepository
                .findBySpeakingSession_SessionCodeAndSpeakingSession_StatusAndSpeakingSession_User_Id(
                        speakingSessionSessionCode,
                        speakingSessionStatus,
                        speakingSessionUserId
                )
                .map(speakingSessionAssessmentEntityMapper::entityToDomain);
    }

    @Override
    public Optional<SpeakingSessionAssessment> findBySpeakingSession_Id(Long speakingSessionId) {
        return speakingSessionAssessmentJpaRepository
                .findBySpeakingSession_Id(speakingSessionId)
                .map(speakingSessionAssessmentEntityMapper::entityToDomain);
    }
}
