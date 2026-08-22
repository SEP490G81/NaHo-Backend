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
import org.naho.question.port.in.CompleteSpeakingQuestionInputPort;
import org.naho.question.port.out.AnswerHistoryRepositoryPort;
import org.naho.question.port.out.AnswerHistoryResultMapper;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.naho.speech.azure.port.out.SpeechAssessmentRepositoryPort;
import org.naho.speech.azure.port.out.TextToSpeechServicePort;
import org.naho.speech.llm.conversation.adapter.AzureSpeechToTextAdapter;
import org.naho.speech.llm.conversation.adapter.OpenAiChatAdapter;
import org.naho.speech.llm.conversation.adapter.OpenAiScoringAdapter;
import org.naho.speech.llm.conversation.constant.OpenAiConfigProperties;
import org.naho.speech.llm.conversation.helper.SpeakingSessionHelper;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionMessageResultMapper;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionResultMapper;
import org.naho.speech.llm.conversation.port.in.CrudSpeakingSessionInputPort;
import org.naho.speech.llm.conversation.port.in.EndSessionInputPort;
import org.naho.speech.llm.conversation.port.in.SpeakingSessionCleanupInputPort;
import org.naho.speech.llm.conversation.port.in.SpeakingSessionInputPort;
import org.naho.speech.llm.conversation.port.out.*;
import org.naho.speech.llm.conversation.usecase.CrudSpeakingSessionUseCase;
import org.naho.speech.llm.conversation.usecase.EndSessionUseCase;
import org.naho.speech.llm.conversation.usecase.SpeakingSessionCleanupUseCase;
import org.naho.speech.llm.conversation.usecase.SpeakingSessionUseCase;
import org.naho.speech.llm.conversation.validator.SpeakingSessionValidator;
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
    public AiScoringPort aiScoringPort(OpenAiConfigProperties openAiConfigProperties) {
        return new OpenAiScoringAdapter(openAiConfigProperties);
    }

    @Bean
    public SpeechToTextPort speechToTextPort(AzureSpeechServicePort azureSpeechServicePort) {
        return new AzureSpeechToTextAdapter(azureSpeechServicePort);
    }

    @Bean
    public SpeakingSessionValidator sessionValidator(
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            GetActiveSubscriptionInputPort getActiveSubscriptionInputPort,
            UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort
    ) {
        return new SpeakingSessionValidator(
                speakingSessionRepositoryPort,
                getActiveSubscriptionInputPort,
                userDailyAiUsageRepositoryPort
        );
    }

    @Bean
    public SpeakingSessionHelper speakingSessionHelper(
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            TextToSpeechServicePort textToSpeechServicePort
    ) {
        return new SpeakingSessionHelper(
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
            SpeakingSessionMessageRepositoryPort speakingSessionMessageRepositoryPort,
            SpeechToTextPort speechToTextPort,
            PersonaRepositoryPort personaRepositoryPort,
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            FileRepositoryPort fileRepositoryPort,
            UploadFileInputPort uploadFileInputPort,
            SpeakingSessionValidator speakingSessionValidator,
            SpeakingSessionHelper speakingSessionHelper,
            SpeakingSessionResultMapper speakingSessionResultMapper,
            TransactionPort transactionPort,
            SpeakingSessionMessageResultMapper speakingSessionMessageResultMapper
    ) {
        return new SpeakingSessionUseCase(
                aiChatPort,
                speakingSessionMessageRepositoryPort,
                speechToTextPort,
                personaRepositoryPort,
                speakingSessionRepositoryPort,
                fileRepositoryPort,
                uploadFileInputPort,
                speakingSessionValidator,
                speakingSessionHelper,
                speakingSessionResultMapper,
                transactionPort,
                speakingSessionMessageResultMapper
        );
    }

    @Bean
    public EndSessionInputPort endSessionInputPort(
            AiScoringPort aiScoringPort,
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            PersonaRepositoryPort personaRepositoryPort,
            SpeakingSessionHelper speakingSessionHelper,
            SpeakingSessionResultMapper speakingSessionResultMapper,
            CrudUserDailyMissionInputPort crudUserDailyMissionInputPort,
            UserLearningStreakInputPort userLearningStreakInputPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            TransactionPort transactionPort,
            SpeakingSessionMessageRepositoryPort speakingSessionMessageRepositoryPort,
            AiChatPort aiChatPort
    ) {
        return new EndSessionUseCase(
                aiScoringPort,
                speakingSessionRepositoryPort,
                personaRepositoryPort,
                speakingSessionHelper,
                speakingSessionResultMapper,
                crudUserDailyMissionInputPort,
                userLearningStreakInputPort,
                userLearningProgressRepositoryPort,
                transactionPort,
                speakingSessionMessageRepositoryPort,
                aiChatPort
        );
    }

    @Bean
    public SpeakingAnalysisHelper questionSpeakingAnalysisHelper(
            SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort,
            BookRepositoryPort bookRepositoryPort,
            TopicRepositoryPort topicRepositoryPort,
            LessonRepositoryPort lessonRepositoryPort,
            ObjectiveRepositoryPort objectiveRepositoryPort
    ) {
        return new SpeakingAnalysisHelper(
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
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            CompleteSpeakingQuestionInputPort completeSpeakingQuestionInputPort
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
                userLearningProgressRepositoryPort,
                completeSpeakingQuestionInputPort
        );
    }

    @Bean
    public SpeakingSessionCleanupInputPort speakingSessionCleanupInputPort(
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            SpeakingSessionValidator speakingSessionValidator
    ) {
        return new SpeakingSessionCleanupUseCase(
                speakingSessionRepositoryPort,
                speakingSessionValidator
        );
    }

    @Bean
    public CrudSpeakingSessionInputPort crudSpeakingSessionInputPort(
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            SpeakingSessionResultMapper speakingSessionResultMapper
    ) {
        return new CrudSpeakingSessionUseCase(speakingSessionRepositoryPort, speakingSessionResultMapper);
    }
}


