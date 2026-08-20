package org.naho.config.application;

import org.naho.book.port.out.BookRepositoryPort;
import org.naho.book.port.out.LessonRepositoryPort;
import org.naho.book.port.out.ObjectiveRepositoryPort;
import org.naho.book.port.out.TopicRepositoryPort;
import org.naho.daily.port.in.CrudUserDailyMissionInputPort;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.learning.port.in.UserLearningStreakInputPort;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.question.port.out.AnswerHistoryRepositoryPort;
import org.naho.question.port.out.AnswerHistoryResultMapper;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.azure.port.out.SpeechAssessmentRepositoryPort;
import org.naho.speech.azure.port.out.TextToSpeechServicePort;
import org.naho.speech.llm.conversation.adapter.AzureSpeechToTextAdapter;
import org.naho.speech.llm.conversation.adapter.InMemorySessionStore;
import org.naho.speech.llm.conversation.adapter.OpenAiChatAdapter;
import org.naho.speech.llm.conversation.adapter.OpenAiScoringAdapter;
import org.naho.speech.llm.conversation.constant.OpenAiConfigProperties;
import org.naho.speech.llm.conversation.helper.SpeakingSessionHelper;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionMessageResultMapper;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionResultMapper;
import org.naho.speech.llm.conversation.port.in.EndSessionInputPort;
import org.naho.speech.llm.conversation.port.in.SpeakingSessionCleanupInputPort;
import org.naho.speech.llm.conversation.port.in.SpeakingSessionInputPort;
import org.naho.speech.llm.conversation.port.in.SuggestedTopicsInputPort;
import org.naho.speech.llm.conversation.port.out.*;
import org.naho.speech.llm.conversation.usecase.EndSessionUseCase;
import org.naho.speech.llm.conversation.usecase.SpeakingSessionCleanupUseCase;
import org.naho.speech.llm.conversation.usecase.SpeakingSessionUseCase;
import org.naho.speech.llm.conversation.usecase.SuggestedTopicsUseCase;
import org.naho.speech.llm.conversation.validator.SessionValidator;
import org.naho.speech.llm.question.helper.SpeakingAnalysisHelper;
import org.naho.speech.llm.question.port.out.AiFeedbackRepositoryPort;
import org.naho.speech.llm.question.port.out.AiQuestionAnalysisPort;
import org.naho.speech.llm.question.usecase.SpeakingAnalysisUseCase;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.port.out.UserDailyAiUsageRepositoryPort;
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

    @Bean
    public SessionValidator sessionValidator(
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            SessionStorePort sessionStorePort,
            GetActiveSubscriptionInputPort getActiveSubscriptionInputPort,
            UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort
    ) {
        return new SessionValidator(
                speakingSessionRepositoryPort,
                sessionStorePort,
                getActiveSubscriptionInputPort,
                userDailyAiUsageRepositoryPort
        );
    }

    @Bean
    public SpeakingSessionHelper speakingSessionHelper(
            SessionStorePort sessionStorePort,
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            TextToSpeechServicePort textToSpeechServicePort
    ) {
        return new SpeakingSessionHelper(
                sessionStorePort,
                speakingSessionRepositoryPort,
                textToSpeechServicePort
        );
    }

    @Bean
    public SpeakingSessionMessageResultMapper speakingSessionMessageResultMapper(
            SpeakingSessionHelper speakingSessionHelper,
            FileStorageServicePort fileStorageServicePort
    ) {
        return new SpeakingSessionMessageResultMapper(speakingSessionHelper, fileStorageServicePort);
    }

    @Bean
    public SpeakingSessionResultMapper speakingSessionResultMapper(
            SpeakingSessionMessageRepositoryPort speakingSessionMessageRepositoryPort,
            SpeakingSessionMessageResultMapper speakingSessionMessageResultMapper
    ) {
        return new SpeakingSessionResultMapper(
                speakingSessionMessageRepositoryPort,
                speakingSessionMessageResultMapper
        );
    }

    // ─── Input Port UseCases ─────────────────────────────────────

    @Bean
    public SpeakingSessionInputPort speakingSessionInputPort(
            AiChatPort aiChatPort,
            SessionStorePort sessionStorePort,
            SpeechToTextPort speechToTextPort,
            PersonaRepositoryPort personaRepositoryPort,
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            FileRepositoryPort fileRepositoryPort,
            UploadFileInputPort uploadFileInputPort,
            SessionValidator sessionValidator,
            SpeakingSessionHelper speakingSessionHelper,
            SpeakingSessionResultMapper speakingSessionResultMapper
    ) {
        return new SpeakingSessionUseCase(
                aiChatPort,
                sessionStorePort,
                speechToTextPort,
                personaRepositoryPort,
                speakingSessionRepositoryPort,
                fileRepositoryPort,
                uploadFileInputPort,
                sessionValidator,
                speakingSessionHelper,
                speakingSessionResultMapper
        );
    }

    @Bean
    public EndSessionInputPort endSessionInputPort(
            SessionStorePort sessionStorePort,
            AiScoringPort aiScoringPort,
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            CrudUserDailyMissionInputPort crudUserDailyMissionInputPort,
            UserLearningStreakInputPort userLearningStreakInputPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            TransactionPort transactionPort
    ) {
        return new EndSessionUseCase(
                sessionStorePort,
                aiScoringPort,
                speakingSessionRepositoryPort,
                crudUserDailyMissionInputPort,
                userLearningStreakInputPort,
                userLearningProgressRepositoryPort,
                transactionPort
        );
    }

    @Bean
    public SuggestedTopicsInputPort suggestedTopicsInputPort() {
        return new SuggestedTopicsUseCase();
    }

    @Bean
    public org.naho.speech.llm.question.helper.SpeakingAnalysisHelper questionSpeakingAnalysisHelper(
            SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort,
            BookRepositoryPort bookRepositoryPort,
            TopicRepositoryPort topicRepositoryPort,
            LessonRepositoryPort lessonRepositoryPort,
            ObjectiveRepositoryPort objectiveRepositoryPort
    ) {
        return new org.naho.speech.llm.question.helper.SpeakingAnalysisHelper(
                speakingQuestionRepositoryPort,
                bookRepositoryPort,
                topicRepositoryPort,
                lessonRepositoryPort,
                objectiveRepositoryPort
        );
    }

    @Bean
    public org.naho.speech.llm.question.port.in.SpeakingAnalysisInputPort speakingAnalysisInputPort(
            UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort,
            UploadFileInputPort uploadFileInputPort,
            TransactionPort transactionPort,
            AzureSpeechServicePort azureSpeechServicePort,
            AiFeedbackRepositoryPort aiFeedbackRepositoryPort,
            AiQuestionAnalysisPort aiQuestionAnalysisPort,
            SpeakingAnalysisHelper speakingAnalysisHelper,
            SpeechAssessmentRepositoryPort speechAssessmentRepositoryPort,
            AnswerHistoryRepositoryPort answerHistoryRepositoryPort,
            AnswerHistoryResultMapper answerHistoryResultMapper,
            FileRepositoryPort fileRepositoryPort,
            LearningPathNodeRepositoryPort learningPathNodeRepositoryPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort
    ) {
        return new SpeakingAnalysisUseCase(
                userDailyAiUsageRepositoryPort,
                uploadFileInputPort,
                transactionPort,
                azureSpeechServicePort,
                aiFeedbackRepositoryPort,
                aiQuestionAnalysisPort,
                speakingAnalysisHelper,
                speechAssessmentRepositoryPort,
                answerHistoryRepositoryPort,
                answerHistoryResultMapper,
                fileRepositoryPort,
                learningPathNodeRepositoryPort,
                userLearningProgressRepositoryPort
        );
    }

    @Bean
    public SpeakingSessionCleanupInputPort speakingSessionCleanupInputPort(
            SessionStorePort sessionStorePort,
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            SessionValidator sessionValidator
    ) {
        return new SpeakingSessionCleanupUseCase(
                sessionStorePort,
                speakingSessionRepositoryPort,
                sessionValidator
        );
    }
}


