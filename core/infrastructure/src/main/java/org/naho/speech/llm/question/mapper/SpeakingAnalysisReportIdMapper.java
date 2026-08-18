package org.naho.speech.llm.question.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.speech.llm.question.entity.SpeakingAnalysisReportEntity;

@Mapper(componentModel = "spring")
public abstract class SpeakingAnalysisReportIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public SpeakingAnalysisReportEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(SpeakingAnalysisReportEntity.class, id);
    }

    public Long entityToId(SpeakingAnalysisReportEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
