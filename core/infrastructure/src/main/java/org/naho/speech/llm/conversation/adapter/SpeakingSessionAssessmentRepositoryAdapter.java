package org.naho.speech.llm.conversation.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.speech.llm.conversation.entity.SpeakingImprovedExpressionEntity;
import org.naho.speech.llm.conversation.entity.SpeakingSessionAssessmentEntity;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionAssessmentEntityMapper;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionAssessmentRepositoryPort;
import org.naho.speech.llm.conversation.repository.SpeakingSessionAssessmentJpaRepository;
import org.naho.speech.llm.model.conversation.SpeakingSessionAssessment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpeakingSessionAssessmentRepositoryAdapter implements SpeakingSessionAssessmentRepositoryPort {
    private final SpeakingSessionAssessmentJpaRepository speakingSessionAssessmentJpaRepository;
    private final SpeakingSessionAssessmentEntityMapper speakingSessionAssessmentEntityMapper;

    @Override
    public SpeakingSessionAssessment save(SpeakingSessionAssessment speakingSessionAssessment) {
        SpeakingSessionAssessmentEntity entity = speakingSessionAssessmentEntityMapper.domainToEntity(speakingSessionAssessment);

        for (SpeakingImprovedExpressionEntity speakingImprovedExpressionEntity : entity.getSpeakingImprovedExpressions()) {
            speakingImprovedExpressionEntity.setSpeakingSessionAssessment(entity);
        }

        SpeakingSessionAssessmentEntity savedEntity = speakingSessionAssessmentJpaRepository.save(entity);
        return speakingSessionAssessmentEntityMapper.entityToDomain(savedEntity);
    }
}
