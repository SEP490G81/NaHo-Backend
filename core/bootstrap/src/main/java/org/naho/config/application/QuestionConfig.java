package org.naho.config.application;

import org.naho.question.adapter.QuestionListRepositoryAdapter;
import org.naho.question.adapter.QuestionRepositoryAdapter;
import org.naho.question.port.in.*;
import org.naho.question.usecase.*;
import org.naho.shared.port.out.EventPublisherPort;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.llm.port.out.AiChatPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuestionConfig {

    @Bean
    public SuggestCustomQuestionUseCase suggestCustomQuestionUseCase(
            AiChatPort aiChatPort
    ) {
        return new SuggestCustomQuestionUseCase(aiChatPort);
    }

    @Bean
    public CreateQuestionInputPort createQuestionInputPort(QuestionRepositoryAdapter questionRepositoryAdapter,
                                                           org.naho.book.adapter.ObjectiveRepositoryAdapter objectiveRepositoryAdapter) {
        return new CreateQuestionUseCase(questionRepositoryAdapter, objectiveRepositoryAdapter);
    }

    @Bean
    public UpdateQuestionInputPort updateQuestionInputPort(QuestionRepositoryAdapter questionRepositoryAdapter) {
        return new UpdateQuestionUseCase(questionRepositoryAdapter);
    }

    @Bean
    public DeleteQuestionInputPort deleteQuestionInputPort(QuestionRepositoryAdapter questionRepositoryAdapter,
                                                           TransactionPort transactionPort) {
        return new DeleteQuestionUseCase(questionRepositoryAdapter, transactionPort);
    }

    @Bean
    public ChangeQuestionStatusInputPort changeQuestionStatusInputPort(QuestionRepositoryAdapter questionRepositoryAdapter,
                                                                       EventPublisherPort eventPublisherPort,
                                                                       TransactionPort transactionPort) {
        return new ChangeQuestionStatusUseCase(questionRepositoryAdapter, eventPublisherPort, transactionPort);
    }

    @Bean
    public SearchQuestionsInputPort searchQuestionsInputPort(QuestionListRepositoryAdapter questionListRepositoryAdapter) {
        return new SearchQuestionsUseCase(questionListRepositoryAdapter);
    }
}
