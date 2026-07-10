package org.naho.config.application;

import org.naho.book.adapter.TopicListRepositoryAdapter;
import org.naho.book.adapter.TopicRepositoryAdapter;
import org.naho.book.port.in.*;
import org.naho.book.usecase.*;
import org.naho.question.port.out.QuestionRepositoryPort;
import org.naho.shared.port.out.TransactionPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicConfig {

    @Bean
    public CreateTopicInputPort createTopicInputPort(TopicRepositoryAdapter topicRepositoryAdapter) {
        return new CreateTopicUseCase(topicRepositoryAdapter);
    }

    @Bean
    public ListTopicInputPort listTopicUseCasePort(TopicListRepositoryAdapter topicListRepositoryAdapter) {
        return new ListTopicUsecase(topicListRepositoryAdapter);
    }

    @Bean
    public GetTopicDetailInputPort getTopicDetailInputPort(TopicRepositoryAdapter topicRepositoryAdapter) {
        return new GetTopicDetailUseCase(topicRepositoryAdapter);
    }

    @Bean
    public UpdateTopicInputPort updateTopicInputPort(TopicRepositoryAdapter topicRepositoryAdapter) {
        return new UpdateTopicUseCase(topicRepositoryAdapter);
    }

    @Bean
    public DeleteTopicInputPort deleteTopicInputPort(TopicRepositoryAdapter topicRepositoryAdapter,
                                                     QuestionRepositoryPort questionRepositoryPort,
                                                     TransactionPort transactionPort) {
        return new DeleteTopicUseCase(topicRepositoryAdapter, questionRepositoryPort, transactionPort);
    }
}
