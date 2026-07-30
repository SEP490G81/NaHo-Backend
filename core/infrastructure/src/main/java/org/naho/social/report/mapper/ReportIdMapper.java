package org.naho.social.report.mapper;

import org.mapstruct.Mapper;
import org.naho.social.report.entity.ReportEntity;

@Mapper(componentModel = "spring")
public interface ReportIdMapper {
    default ReportEntity idToEntity(Long id) {
        if (id == null) {
            return null;
        }
        ReportEntity entity = new ReportEntity();
        entity.setId(id);
        return entity;
    }

    default Long entityToId(ReportEntity entity) {
        return entity == null ? null : entity.getId();
    }
}
