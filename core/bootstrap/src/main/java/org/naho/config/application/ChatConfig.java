package org.naho.config.application;

import org.naho.book.port.out.BookRepositoryPort;
import org.naho.book.port.out.LessonRepositoryPort;
import org.naho.book.port.out.ObjectiveRepositoryPort;
import org.naho.book.port.out.TopicRepositoryPort;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.furigana.port.out.FuriganaGenerationPort;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.question.port.in.CompleteSpeakingQuestionInputPort;
import org.naho.question.port.out.AnswerHistoryRepositoryPort;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.azure.port.out.TextToSpeechServicePort;
import org.naho.speech.llm.adapter.*;
import org.naho.speech.llm.constant.OpenAiConfigProperties;
import org.naho.speech.llm.port.in.*;
import org.naho.speech.llm.port.out.*;
import org.naho.speech.llm.usecase.*;
import org.naho.subscription.port.in.CrudUserDailyAiUsageInputPort;
import org.naho.subscription.port.out.UserDailyAiUsageRepositoryPort;
import org.naho.user.port.out.UserRepositoryPort;
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
            TextToSpeechServicePort textToSpeechServicePort,
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort
    ) {
        return new SpeakingSessionUseCase(
                aiChatPort,
                sessionStorePort,
                speechToTextPort,
                personaRepositoryPort,
                textToSpeechServicePort,
                speakingSessionRepositoryPort
        );
    }

    @Bean
    public EndSessionInputPort endSessionInputPort(
            SessionStorePort sessionStorePort,
            AiScoringPort aiScoringPort,
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort
    ) {
        return new EndSessionUseCase(sessionStorePort, aiScoringPort, speakingSessionRepositoryPort);
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
            FileRepositoryPort fileRepositoryPort,
            AnswerHistoryRepositoryPort answerHistoryRepositoryPort,
            AzureSpeechServicePort azureSpeechServicePort,
            TopicRepositoryPort topicRepositoryPort,
            LessonRepositoryPort lessonRepositoryPort,
            ObjectiveRepositoryPort objectiveRepositoryPort,
            BookRepositoryPort bookRepositoryPort,
            LearningPathNodeRepositoryPort learningPathNodeRepositoryPort,
            AiAnalysisPort aiAnalysisPort,
            TransactionPort transactionPort,
            CompleteSpeakingQuestionInputPort completeSpeakingQuestionInputPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            FileResultMapperPort fileResultMapperPort,
            UploadFileInputPort uploadFileInputPort,
            CrudUserDailyAiUsageInputPort crudUserDailyAiUsageInputPort,
            UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort,
            FuriganaGenerationPort furiganaGenerationPort
    ) {
        return new SpeakingAnalysisUseCase(
                userRepositoryPort,
                questionRepositoryPort,
                fileRepositoryPort,
                answerHistoryRepositoryPort,
                azureSpeechServicePort,
                topicRepositoryPort,
                lessonRepositoryPort,
                objectiveRepositoryPort,
                bookRepositoryPort,
                learningPathNodeRepositoryPort,
                aiAnalysisPort,
                transactionPort,
                completeSpeakingQuestionInputPort,
                userLearningProgressRepositoryPort,
                fileResultMapperPort,
                uploadFileInputPort,
                crudUserDailyAiUsageInputPort,
                userDailyAiUsageRepositoryPort,
                furiganaGenerationPort
        );
    }

    @Bean
    public SpeakingSessionCleanupInputPort speakingSessionCleanupInputPort(
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            TransactionPort transactionPort,
            SessionStorePort sessionStorePort
    ) {
        return new SpeakingSessionCleanupUseCase(speakingSessionRepositoryPort, transactionPort, sessionStorePort);
    }
}


