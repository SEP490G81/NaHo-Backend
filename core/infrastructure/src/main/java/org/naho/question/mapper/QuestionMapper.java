package org.naho.question.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.naho.question.command.SearchQuestionsCommand;
import org.naho.question.result.QuestionListItemResult;

import java.util.List;

@Mapper
public interface QuestionMapper {

    long countQuestions(@Param("query") SearchQuestionsCommand query);

    List<QuestionListItemResult> findQuestions(@Param("query") SearchQuestionsCommand query,
                                               @Param("offset") int offset,
                                               @Param("limit") int limit);
}
