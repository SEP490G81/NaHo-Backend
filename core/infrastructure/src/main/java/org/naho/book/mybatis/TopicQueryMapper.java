package org.naho.book.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.naho.book.command.ListTopicCommand;
import org.naho.book.entity.TopicEntity;
import org.naho.book.result.TopicResult;

import java.util.List;
import java.util.Optional;

@Mapper
public interface TopicQueryMapper {
    long countTopics(@Param("query") ListTopicCommand query);

    List<TopicResult> findTopics(@Param("query") ListTopicCommand query, @Param("offset") int offset);

    Optional<TopicEntity> findBySpeakingQuestionId(@Param("speakingQuestionId") Long speakingQuestionId);
}
