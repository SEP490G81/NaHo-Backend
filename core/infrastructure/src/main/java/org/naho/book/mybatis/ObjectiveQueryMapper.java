package org.naho.book.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.naho.book.entity.ObjectiveEntity;

import java.util.Optional;

@Mapper
public interface ObjectiveQueryMapper {
    Optional<ObjectiveEntity> findBySpeakingQuestionId(@Param("speakingQuestionId") Long speakingQuestionId);
}
