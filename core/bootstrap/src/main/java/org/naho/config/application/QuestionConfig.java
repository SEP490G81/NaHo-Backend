package org.naho.config.application;

import org.naho.question.port.in.SuggestCustomQuestionInputPort;
import org.naho.question.usecase.SuggestCustomQuestionUseCase;
import org.naho.speech.llm.port.out.AiChatPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuestionConfig {

    @Bean
    public SuggestCustomQuestionInputPort suggestCustomQuestionInputPort(AiChatPort aiChatPort) {
        return new SuggestCustomQuestionUseCase(aiChatPort);
    }
}
