package org.naho.speech.azure.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.speech.azure.entity.SpeechAssessmentEntity;
import org.naho.speech.azure.entity.WordAssessmentEntity;
import org.naho.speech.azure.mapper.SpeechAssessmentEntityMapper;
import org.naho.speech.azure.model.SpeechAssessment;
import org.naho.speech.azure.port.out.SpeechAssessmentRepositoryPort;
import org.naho.speech.azure.repository.SpeechAssessmentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SpeechAssessmentRepositoryAdapter implements SpeechAssessmentRepositoryPort {

    private final SpeechAssessmentJpaRepository speechAssessmentJpaRepository;
    private final SpeechAssessmentEntityMapper speechAssessmentEntityMapper;

    @Override
    public SpeechAssessment createNew(SpeechAssessment speechAssessment) {
        SpeechAssessmentEntity entity = speechAssessmentEntityMapper.domainToEntity(speechAssessment);
        for (WordAssessmentEntity wordAssessment : entity.getWords()) {
            wordAssessment.setSpeechAssessment(entity);
        }
        SpeechAssessmentEntity savedEntity = speechAssessmentJpaRepository.save(entity);
        return speechAssessmentEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public Optional<SpeechAssessment> findById(Long id) {
        return speechAssessmentJpaRepository
                .findById(id)
                .map(speechAssessmentEntityMapper::entityToDomain);
    }
}

