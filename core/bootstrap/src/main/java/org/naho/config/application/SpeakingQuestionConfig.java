package org.naho.config.application;

import org.naho.daily.port.in.CrudUserDailyMissionInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.furigana.port.out.FuriganaGenerationPort;
import org.naho.grammar.mapper.GrammarResultMapper;
import org.naho.grammar.port.out.GrammarRepositoryPort;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.learning.port.in.UserLearningStreakInputPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.learning.port.out.UserNodeProgressRepositoryPort;
import org.naho.point.port.in.CrudPointHistoryInputPort;
import org.naho.question.adapter.SpeakingQuestionListRepositoryAdapter;
import org.naho.question.mapper.SpeakingQuestionResultMapper;
import org.naho.question.port.in.*;
import org.naho.question.port.out.AnswerHistoryRepositoryPort;
import org.naho.question.port.out.AnswerHistoryResultMapper;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.usecase.*;
import org.naho.shared.port.out.TransactionPort;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.vocabulary.mapper.VocabularyResultMapper;
import org.naho.vocabulary.port.out.VocabularyRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpeakingQuestionConfig {

    @Bean
    public CrudAnswerHistoryInputPort crudAnswerHistoryInputPort(
            AnswerHistoryRepositoryPort answerHistoryRepositoryPort,
            FileRepositoryPort fileRepositoryPort,
            FileStorageServicePort fileStorageServicePort,
            AnswerHistoryResultMapper answerHistoryResultMapper
    ) {
        return new CrudAnswerHistoryUseCase(
                answerHistoryRepositoryPort,
                fileRepositoryPort,
                fileStorageServicePort,
                answerHistoryResultMapper
        );
    }

    @Bean
    public UpdateSpeakingQuestionInputPort updateSpeakingQuestionInputPort(
            SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort,
            VocabularyRepositoryPort vocabularyRepositoryPort,
            GrammarRepositoryPort grammarRepositoryPort,
            TransactionPort transactionPort,
            FuriganaGenerationPort furiganaGenerationPort
    ) {
        return new UpdateSpeakingQuestionUseCase(
                speakingQuestionRepositoryPort,
                vocabularyRepositoryPort,
                grammarRepositoryPort,
                transactionPort,
                furiganaGenerationPort
        );
    }

    @Bean
    public SearchSpeakingQuestionsInputPort searchSpeakingQuestionsInputPort(
            SpeakingQuestionListRepositoryAdapter speakingQuestionListRepositoryAdapter) {
        return new SearchSpeakingQuestionsUseCase(speakingQuestionListRepositoryAdapter);
    }

    @Bean
    public SpeakingQuestionResultMapper speakingQuestionResultMapper(
            GrammarResultMapper grammarResultMapper,
            VocabularyResultMapper vocabularyResultMapper
    ) {
        return new SpeakingQuestionResultMapper(grammarResultMapper, vocabularyResultMapper);
    }

    @Bean
    public GetSpeakingQuestionInputPort getSpeakingQuestionInputPort(
            SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort,
            SpeakingQuestionResultMapper speakingQuestionResultMapper,
            GetActiveSubscriptionInputPort getActiveSubscriptionInputPort,
            org.naho.user.port.out.RoleRepositoryPort roleRepositoryPort
    ) {
        return new GetSpeakingQuestionUseCase(
                speakingQuestionRepositoryPort,
                speakingQuestionResultMapper,
                getActiveSubscriptionInputPort,
                roleRepositoryPort
        );
    }

    @Bean
    public CompleteSpeakingQuestionInputPort completeSpeakingQuestionInputPort(
            UserNodeProgressRepositoryPort userNodeProgressRepositoryPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            CrudPointHistoryInputPort crudPointHistoryInputPort,
            TransactionPort transactionPort,
            UserLearningStreakInputPort userLearningStreakInputPort,
            CrudUserLearningProgressInputPort crudUserLearningProgressInputPort,
            CrudUserDailyMissionInputPort crudUserDailyMissionInputPort) {
        return new CompleteSpeakingQuestionUseCase(
                userNodeProgressRepositoryPort,
                userLearningProgressRepositoryPort,
                crudPointHistoryInputPort,
                transactionPort,
                userLearningStreakInputPort,
                crudUserLearningProgressInputPort,
                crudUserDailyMissionInputPort);
    }

    @Bean
    public org.naho.speech.llm.question.mapper.UsedVocabularyAndGrammarResultMapper usedVocabularyAndGrammarResultMapper() {
        return new org.naho.speech.llm.question.mapper.UsedVocabularyAndGrammarResultMapper();
    }

    @Bean
    public org.naho.speech.llm.question.mapper.UserAnswerErrorResultMapper userAnswerErrorResultMapper() {
        return new org.naho.speech.llm.question.mapper.UserAnswerErrorResultMapper();
    }

    @Bean
    public org.naho.speech.llm.question.mapper.AiFeedbackResultMapper aiFeedbackResultMapper(
            org.naho.speech.llm.question.mapper.UsedVocabularyAndGrammarResultMapper usedVocabularyAndGrammarResultMapper,
            org.naho.speech.llm.question.mapper.UserAnswerErrorResultMapper userAnswerErrorResultMapper
    ) {
        return new org.naho.speech.llm.question.mapper.AiFeedbackResultMapper(
                usedVocabularyAndGrammarResultMapper,
                userAnswerErrorResultMapper
        );
    }

    @Bean
    public org.naho.speech.llm.question.port.in.CrudAiFeedbackInputPort crudAiFeedbackInputPort(
            org.naho.speech.llm.question.port.out.AiFeedbackRepositoryPort aiFeedbackRepositoryPort,
            org.naho.speech.llm.question.mapper.AiFeedbackResultMapper aiFeedbackResultMapper
    ) {
        return new org.naho.speech.llm.question.usecase.CrudAiFeedbackUseCase(
                aiFeedbackRepositoryPort,
                aiFeedbackResultMapper
        );
    }
}



