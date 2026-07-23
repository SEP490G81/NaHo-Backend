package org.naho.config.application;

import org.naho.book.port.out.TopicRepositoryPort;
import org.naho.book.port.out.BookRepositoryPort;
import org.naho.book.port.out.LessonRepositoryPort;
import org.naho.book.port.out.ObjectiveRepositoryPort;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.file.port.in.FileStorageInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.furigana.port.out.FuriganaGenerationPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.llm.adapter.*;
import org.naho.speech.llm.constant.OpenAiConfigProperties;
import org.naho.speech.llm.port.in.EndSessionInputPort;
import org.naho.speech.llm.port.in.SpeakingAnalysisInputPort;
import org.naho.speech.llm.port.in.SpeakingSessionInputPort;
import org.naho.speech.llm.port.in.SuggestedTopicsInputPort;
import org.naho.speech.llm.port.out.*;
import org.naho.speech.llm.usecase.EndSessionUseCase;
import org.naho.speech.llm.usecase.SpeakingAnalysisUseCase;
import org.naho.speech.llm.usecase.SpeakingSessionUseCase;
import org.naho.speech.llm.usecase.SuggestedTopicsUseCase;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.speech.azure.port.out.TextToSpeechServicePort;
import org.naho.question.port.in.CompleteSpeakingQuestionInputPort;
import org.naho.shared.port.out.TransactionPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bootstrap Configuration: Liên kết các UseCase, Port, Adapter cho module AI Speaking.
 * <p>
 * Luồng wiring:
 * OpenAiConfigProperties → OpenAiProperties (record)
 * → OpenAiChatAdapter (AiChatPort)
 * → OpenAiScoringAdapter (AiScoringPort)
 * <p>
 * InMemorySessionStore (SessionStorePort)
 * AzureSpeechToTextAdapter (SpeechToTextPort) ← bridge với AzureSpeechService
 * <p>
 * SpeakingSessionUseCase (SpeakingSessionInputPort)
 * EndSessionUseCase (EndSessionInputPort)
 * SuggestedTopicsUseCase (SuggestedTopicsInputPort)
 */
@Configuration
public class ChatConfig {
    // ─── Output Port Adapters ────────────────────────────────────
    @Bean
    public AiChatPort aiChatPort(OpenAiConfigProperties openAiConfigProperties) {
        return new OpenAiChatAdapter(openAiConfigProperties);
    }

    @Bean
    public SessionStorePort sessionStorePort() {
        return new InMemorySessionStore();
    }

    @Bean
    public AiScoringPort aiScoringPort(OpenAiConfigProperties openAiConfigProperties) {
        return new OpenAiScoringAdapter(openAiConfigProperties);
    }

    @Bean
    public SpeechToTextPort speechToTextPort(AzureSpeechServicePort azureSpeechServicePort) {
        return new AzureSpeechToTextAdapter(azureSpeechServicePort);
    }

    // ─── Input Port UseCases ─────────────────────────────────────

    @Bean
    public SpeakingSessionInputPort speakingSessionInputPort(
            AiChatPort aiChatPort,
            SessionStorePort sessionStorePort,
            SpeechToTextPort speechToTextPort,
            PersonaRepositoryPort personaRepositoryPort,
            TextToSpeechServicePort textToSpeechServicePort
    ) {
        return new SpeakingSessionUseCase(aiChatPort, sessionStorePort, speechToTextPort, personaRepositoryPort, textToSpeechServicePort);
    }

    @Bean
    public EndSessionInputPort endSessionInputPort(
            SessionStorePort sessionStorePort,
            AiScoringPort aiScoringPort
    ) {
        return new EndSessionUseCase(sessionStorePort, aiScoringPort);
    }

    @Bean
    public SuggestedTopicsInputPort suggestedTopicsInputPort() {
        return new SuggestedTopicsUseCase();
    }

    @Bean
    public AiAnalysisPort aiAnalysisPort(OpenAiConfigProperties openAiConfigProperties) {
        return new OpenAiAnalysisAdapter(openAiConfigProperties);
    }

    @Bean
    public SpeakingAnalysisInputPort speakingAnalysisInputPort(
            UserRepositoryPort userRepositoryPort,
            SpeakingQuestionRepositoryPort questionRepositoryPort,
            TopicRepositoryPort topicRepositoryPort,
            LessonRepositoryPort lessonRepositoryPort,
            ObjectiveRepositoryPort objectiveRepositoryPort,
            BookRepositoryPort bookRepositoryPort,
            LearningPathNodeRepositoryPort learningPathNodeRepositoryPort,
            FileStorageInputPort fileStorageInputPort,
            FileRepositoryPort fileRepositoryPort,
            AnswerHistoryRepositoryPort answerHistoryRepositoryPort,
            AzureSpeechServicePort azureSpeechServicePort,
            AiAnalysisPort aiAnalysisPort,
            FuriganaGenerationPort furiganaGenerationPort,
            TransactionPort transactionPort,
            CompleteSpeakingQuestionInputPort completeSpeakingQuestionInputPort
    ) {
        return new SpeakingAnalysisUseCase(
                userRepositoryPort,
                questionRepositoryPort,
                fileStorageInputPort,
                fileRepositoryPort,
                answerHistoryRepositoryPort,
                azureSpeechServicePort,
                topicRepositoryPort,
                lessonRepositoryPort,
                objectiveRepositoryPort,
                bookRepositoryPort,
                learningPathNodeRepositoryPort,
                aiAnalysisPort,
                furiganaGenerationPort,
                transactionPort,
                completeSpeakingQuestionInputPort
        );
    }
}
