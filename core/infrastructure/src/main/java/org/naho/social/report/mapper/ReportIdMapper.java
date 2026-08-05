package org.naho.social.report.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.social.report.entity.ReportEntity;

@Mapper(componentModel = "spring")
public abstract class ReportIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public ReportEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(ReportEntity.class, id);
    }

    public Long entityToId(ReportEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
