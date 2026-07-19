package org.naho.book.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.naho.book.entity.LessonEntity;

import java.util.Optional;

@Mapper
public interface LessonQueryMapper {
    Optional<LessonEntity> findBySpeakingQuestionId(@Param("speakingQuestionId") Long speakingQuestionId);
}
