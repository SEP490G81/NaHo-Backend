package org.naho.question.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.question.command.SearchSpeakingQuestionsCommand;
import org.naho.question.mybatis.SpeakingQuestionQueryMapper;
import org.naho.question.port.out.SpeakingQuestionListRepositoryPort;
import org.naho.question.result.SpeakingQuestionListItemResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SpeakingQuestionListRepositoryAdapter implements SpeakingQuestionListRepositoryPort {

    private final SpeakingQuestionQueryMapper speakingQuestionQueryMapper;

    @Override
    public long countSpeakingQuestions(SearchSpeakingQuestionsCommand query) {
        return speakingQuestionQueryMapper.countSpeakingQuestions(query);
    }

    @Override
    public List<SpeakingQuestionListItemResult> findSpeakingQuestions(SearchSpeakingQuestionsCommand query) {
        int offset = (query.page() - 1) * query.size();
        return speakingQuestionQueryMapper.findSpeakingQuestions(query, offset, query.size());
    }
}
