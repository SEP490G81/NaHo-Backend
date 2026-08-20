package org.naho.speech.llm.question.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.speech.llm.model.question.AiFeedback;
import org.naho.speech.llm.question.entity.AiFeedbackEntity;
import org.naho.speech.llm.question.entity.UsedVocabularyAndGrammarEntity;
import org.naho.speech.llm.question.entity.UserAnswerErrorEntity;
import org.naho.speech.llm.question.mapper.AiFeedbackEntityMapper;
import org.naho.speech.llm.question.port.out.AiFeedbackRepositoryPort;
import org.naho.speech.llm.question.repository.AiFeedbackJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AiFeedbackRepositoryAdapter implements AiFeedbackRepositoryPort {

    private final AiFeedbackJpaRepository aiFeedbackJpaRepository;
    private final AiFeedbackEntityMapper aiFeedbackEntityMapper;

    @Override
    public AiFeedback createNew(AiFeedback feedback) {
        AiFeedbackEntity entity = aiFeedbackEntityMapper.domainToEntity(feedback);

        for (UsedVocabularyAndGrammarEntity usedVocabularyAndGrammar : entity.getUsedVocabulariesAndGrammars()) {
            usedVocabularyAndGrammar.setAiFeedback(entity);
        }

        for (UserAnswerErrorEntity userAnswerError : entity.getUserAnswerErrors()) {
            userAnswerError.setAiFeedback(entity);
        }

        AiFeedbackEntity savedEntity = aiFeedbackJpaRepository.save(entity);
        return aiFeedbackEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public Optional<AiFeedback> findById(Long id) {
        return aiFeedbackJpaRepository
                .findById(id)
                .map(aiFeedbackEntityMapper::entityToDomain);
    }
}
