package org.naho.speech.llm.question.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.speech.llm.model.question.AiFeedback;
import org.naho.speech.llm.question.entity.AiFeedbackEntity;
import org.naho.speech.llm.question.mapper.AiFeedbackEntityMapper;
import org.naho.speech.llm.question.port.out.AiFeedbackRepositoryPort;
import org.naho.speech.llm.question.repository.AiFeedbackJpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiFeedbackRepositoryAdapter implements AiFeedbackRepositoryPort {

    private final AiFeedbackJpaRepository aiFeedbackJpaRepository;
    private final AiFeedbackEntityMapper aiFeedbackEntityMapper;

    @Override
    public AiFeedback createNew(AiFeedback feedback) {
        AiFeedbackEntity entity = aiFeedbackEntityMapper.domainToEntity(feedback);
        AiFeedbackEntity savedEntity = aiFeedbackJpaRepository.save(entity);
        return aiFeedbackEntityMapper.entityToDomain(savedEntity);
    }
}
