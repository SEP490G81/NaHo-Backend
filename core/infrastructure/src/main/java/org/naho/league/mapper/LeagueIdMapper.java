package org.naho.league.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;
import org.naho.league.entity.LeagueEntity;

@Mapper(componentModel = "spring")
public abstract class LeagueIdMapper {

    @PersistenceContext
    protected EntityManager entityManager;

    public LeagueEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(LeagueEntity.class, id);
    }

    public Long entityToId(LeagueEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
