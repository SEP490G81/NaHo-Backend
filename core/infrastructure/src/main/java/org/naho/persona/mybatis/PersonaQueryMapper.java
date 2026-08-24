package org.naho.persona.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.naho.persona.entity.PersonaEntity;

import java.util.Optional;

@Mapper
public interface PersonaQueryMapper {
    Optional<PersonaEntity> findBySessionCode(@Param("sessionCode") String sessionCode);
}
