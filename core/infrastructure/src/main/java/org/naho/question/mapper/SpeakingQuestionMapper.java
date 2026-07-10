package org.naho.question.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.naho.question.command.SearchSpeakingQuestionsCommand;
import org.naho.question.result.SpeakingQuestionListItemResult;

import java.util.List;

@Mapper
public interface SpeakingQuestionMapper {

    long countSpeakingQuestions(@Param("query") SearchSpeakingQuestionsCommand query);

    List<SpeakingQuestionListItemResult> findSpeakingQuestions(@Param("query") SearchSpeakingQuestionsCommand query,
                                                               @Param("offset") int offset,
                                                               @Param("limit") int limit);
}
