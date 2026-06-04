package org.naho.config.application;

import org.naho.speech.topic.adapter.TopicListRepositoryAdapter;
import org.naho.speech.topic.adapter.TopicRepositoryAdapter;
import org.naho.speech.topic.port.in.CreateTopicInputPort;
import org.naho.speech.topic.port.in.ListTopicUseCasePort;
import org.naho.speech.topic.usecase.CreateTopicUseCase;
import org.naho.speech.topic.usecase.ListTopicUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicConfig {

    @Bean
    public CreateTopicInputPort createTopicInputPort(TopicRepositoryAdapter topicRepositoryAdapter) {
        return new CreateTopicUseCase(topicRepositoryAdapter);
    }

    @Bean
    public ListTopicUseCasePort listTopicUseCasePort(TopicListRepositoryAdapter topicListRepositoryAdapter) {
        return new ListTopicUseCase(topicListRepositoryAdapter);
    }
}
