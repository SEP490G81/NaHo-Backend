package org.naho.config.application;

import org.naho.book.mapper.TopicResultMapper;
import org.naho.book.port.in.*;
import org.naho.book.port.out.LessonRepositoryPort;
import org.naho.book.port.out.TopicListRepositoryPort;
import org.naho.book.port.out.TopicRepositoryPort;
import org.naho.book.usecase.*;
import org.naho.furigana.port.out.FuriganaGenerationPort;
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
    public ListTopicInputPort listTopicUseCasePort(
            TopicListRepositoryPort topicListRepositoryPort,
            TopicResultMapper topicResultMapper
    ) {
        return new ListTopicUseCase(topicListRepositoryPort, topicResultMapper);
    }

    @Bean
    public GetTopicDetailInputPort getTopicDetailInputPort(
            TopicRepositoryPort topicRepositoryPort,
            LessonRepositoryPort lessonRepositoryPort
    ) {
        return new GetTopicDetailUseCase(topicRepositoryPort, lessonRepositoryPort);
    }

    @Bean
    public UpdateTopicInputPort updateTopicInputPort(
            TopicRepositoryPort topicRepositoryPort,
            FuriganaGenerationPort furiganaGenerationPort
    ) {
        return new UpdateTopicUseCase(topicRepositoryPort, furiganaGenerationPort);
    }

    @Bean
    public CreateTopicInputPort createTopicInputPort(
            TopicRepositoryPort topicRepositoryPort
    ) {
        return new CreateTopicUseCase(topicRepositoryPort);
    }

    @Bean
    public DeleteTopicInputPort deleteTopicInputPort(
            TopicRepositoryPort topicRepositoryPort,
            SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort,
            TransactionPort transactionPort
    ) {
        return new DeleteTopicUseCase(topicRepositoryPort, speakingQuestionRepositoryPort, transactionPort);
    }
}
