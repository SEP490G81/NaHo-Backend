package org.naho.config.application;

import org.naho.book.adapter.LessonRepositoryAdapter;
import org.naho.book.adapter.TopicListRepositoryAdapter;
import org.naho.book.adapter.TopicRepositoryAdapter;
import org.naho.book.mapper.TopicResultMapper;
import org.naho.book.port.in.*;
import org.naho.book.usecase.*;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.shared.port.out.TransactionPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicConfig {

    @Bean
    public TopicResultMapper topicResultMapper() {
        return new TopicResultMapper();
    }

    @Bean
    public CreateTopicInputPort createTopicInputPort(TopicRepositoryAdapter topicRepositoryAdapter) {
        return new CreateTopicUseCase(topicRepositoryAdapter);
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
    public UpdateTopicInputPort updateTopicInputPort(TopicRepositoryAdapter topicRepositoryAdapter) {
        return new UpdateTopicUseCase(topicRepositoryAdapter);
    }

    @Bean
    public DeleteTopicInputPort deleteTopicInputPort(TopicRepositoryAdapter topicRepositoryAdapter,
                                                     SpeakingQuestionRepositoryPort questionRepositoryPort,
                                                     TransactionPort transactionPort) {
        return new DeleteTopicUseCase(topicRepositoryAdapter, questionRepositoryPort, transactionPort);
    }
}
