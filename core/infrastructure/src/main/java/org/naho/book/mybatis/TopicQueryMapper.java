package org.naho.book.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.naho.book.command.ListTopicCommand;
import org.naho.book.result.TopicListItemResult;

import java.util.List;

@Mapper
public interface TopicQueryMapper {
    long countTopics(@Param("query") ListTopicCommand query);

    List<TopicListItemResult> findTopics(@Param("query") ListTopicCommand query, @Param("offset") int offset);
}
