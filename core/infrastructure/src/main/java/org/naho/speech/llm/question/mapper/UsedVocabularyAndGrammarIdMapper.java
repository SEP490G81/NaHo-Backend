package org.naho.speech.llm.question.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.speech.llm.question.entity.UsedVocabularyAndGrammarEntity;

@Mapper(componentModel = "spring")
public abstract class UsedVocabularyAndGrammarIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public UsedVocabularyAndGrammarEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(UsedVocabularyAndGrammarEntity.class, id);
    }

    public Long entityToId(UsedVocabularyAndGrammarEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
