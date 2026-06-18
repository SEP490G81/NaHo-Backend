package org.naho.topic.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.naho.topic.command.ListTopicCommand;
import org.naho.topic.result.TopicListItemResult;

import java.util.List;

@Mapper
public interface TopicQueryMapper {
    long countTopics(@Param("query") ListTopicCommand query);

    List<TopicListItemResult> findTopics(@Param("query") ListTopicCommand query, @Param("offset") int offset);
}
