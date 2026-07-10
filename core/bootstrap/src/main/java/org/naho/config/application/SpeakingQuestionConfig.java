package org.naho.config.application;

import org.naho.question.adapter.SpeakingQuestionListRepositoryAdapter;
import org.naho.question.adapter.SpeakingQuestionRepositoryAdapter;
import org.naho.question.port.in.*;
import org.naho.question.usecase.*;
import org.naho.shared.port.out.EventPublisherPort;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.llm.port.out.AiChatPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpeakingQuestionConfig {

    @Bean
    public SuggestCustomSpeakingQuestionUseCase suggestCustomSpeakingQuestionUseCase(
            AiChatPort aiChatPort
    ) {
        return new SuggestCustomSpeakingQuestionUseCase(aiChatPort);
    }

    @Bean
    public CreateSpeakingQuestionInputPort createSpeakingQuestionInputPort(SpeakingQuestionRepositoryAdapter speakingQuestionRepositoryAdapter) {
        return new CreateSpeakingQuestionUseCase(speakingQuestionRepositoryAdapter);
    }

    @Bean
    public UpdateSpeakingQuestionInputPort updateSpeakingQuestionInputPort(SpeakingQuestionRepositoryAdapter speakingQuestionRepositoryAdapter) {
        return new UpdateSpeakingQuestionUseCase(speakingQuestionRepositoryAdapter);
    }

    @Bean
    public DeleteSpeakingQuestionInputPort deleteSpeakingQuestionInputPort(SpeakingQuestionRepositoryAdapter speakingQuestionRepositoryAdapter,
                                                                   TransactionPort transactionPort) {
        return new DeleteSpeakingQuestionUseCase(speakingQuestionRepositoryAdapter, transactionPort);
    }

    @Bean
    public ChangeSpeakingQuestionStatusInputPort changeSpeakingQuestionStatusInputPort(SpeakingQuestionRepositoryAdapter speakingQuestionRepositoryAdapter,
                                                                               EventPublisherPort eventPublisherPort,
                                                                               TransactionPort transactionPort) {
        return new ChangeSpeakingQuestionStatusUseCase(speakingQuestionRepositoryAdapter, eventPublisherPort, transactionPort);
    }

    @Bean
    public SearchSpeakingQuestionsInputPort searchSpeakingQuestionsInputPort(SpeakingQuestionListRepositoryAdapter speakingQuestionListRepositoryAdapter) {
        return new SearchSpeakingQuestionsUseCase(speakingQuestionListRepositoryAdapter);
    }
}
