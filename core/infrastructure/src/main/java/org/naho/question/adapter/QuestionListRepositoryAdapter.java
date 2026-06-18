package org.naho.question.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.question.command.SearchQuestionsCommand;
import org.naho.question.mapper.QuestionMapper;
import org.naho.question.port.out.QuestionListRepositoryPort;
import org.naho.question.result.QuestionListItemResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionListRepositoryAdapter implements QuestionListRepositoryPort {

    private final QuestionMapper questionMapper;

    @Override
    public long countQuestions(SearchQuestionsCommand query) {
        return questionMapper.countQuestions(query);
    }

    @Override
    public List<QuestionListItemResult> findQuestions(SearchQuestionsCommand query) {
        int offset = (query.page() - 1) * query.size();
        return questionMapper.findQuestions(query, offset, query.size());
    }
}
