package org.naho.config.application;

import org.naho.book.adapter.LessonRepositoryAdapter;
import org.naho.book.adapter.TopicListRepositoryAdapter;
import org.naho.book.adapter.TopicRepositoryAdapter;
import org.naho.book.mapper.TopicResultMapper;
import org.naho.book.port.in.GetTopicDetailInputPort;
import org.naho.book.port.in.ListTopicInputPort;
import org.naho.book.port.in.UpdateTopicInputPort;
import org.naho.book.usecase.GetTopicDetailUseCase;
import org.naho.book.usecase.ListTopicUseCase;
import org.naho.book.usecase.UpdateTopicUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicConfig {

    @Bean
    public TopicResultMapper topicResultMapper() {
        return new TopicResultMapper();
    }


    @Bean
    public ListTopicInputPort listTopicUseCasePort(
            TopicListRepositoryAdapter topicListRepositoryAdapter,
            TopicResultMapper topicResultMapper
    ) {
        return new ListTopicUseCase(topicListRepositoryAdapter, topicResultMapper);
    }

    @Bean
    public GetTopicDetailInputPort getTopicDetailInputPort(TopicRepositoryAdapter topicRepositoryAdapter, LessonRepositoryAdapter lessonRepositoryAdapter) {
        return new GetTopicDetailUseCase(topicRepositoryAdapter, lessonRepositoryAdapter);
    }

    @Bean
    public UpdateTopicInputPort updateTopicInputPort(TopicRepositoryAdapter topicRepositoryAdapter, org.naho.furigana.port.out.FuriganaGenerationPort furiganaGenerationPort) {
        return new UpdateTopicUseCase(topicRepositoryAdapter, furiganaGenerationPort);
    }


}
